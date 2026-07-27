package br.com.nischor.ledgerxbackend.shared.infrastructure.security.tls;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * Generates an ephemeral RSA key pair and a self-signed X.509 certificate, packaged as a PKCS#12
 * keystore file, so the embedded server can serve TLS without an operator having to provision a
 * certificate up front. Intended for local development/first-run bootstrapping only: browsers and
 * HTTP clients will not trust this certificate unless it is explicitly added to their trust store.
 */
public final class SelfSignedCertificateGenerator {

    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String KEYSTORE_TYPE = "PKCS12";

    /**
     * Prevents instantiation; this class only exposes static factory methods.
     */
    private SelfSignedCertificateGenerator() {
    }

    /**
     * Generates a self-signed RSA certificate for the given common name and stores it, along
     * with its private key, in a new PKCS#12 keystore file.
     *
     * @param commonName       the X.509 subject/issuer common name (and DNS subject alternative name)
     * @param alias            the alias under which the key entry is stored in the keystore
     * @param keystorePassword the password protecting both the keystore and the private key entry
     * @param validity         how long the certificate remains valid, starting from generation time
     * @return the {@link GeneratedKeystore} describing the path and alias of the created keystore
     * @throws IllegalStateException if key generation, certificate signing, or keystore writing fails
     */
    public static GeneratedKeystore generate(String commonName, String alias, char[] keystorePassword,
            Duration validity) {
        try {
            KeyPair keyPair = generateKeyPair();
            X509Certificate certificate = signCertificate(commonName, keyPair, validity);
            Path keystorePath = writeKeystore(alias, keystorePassword, keyPair, certificate);
            return new GeneratedKeystore(keystorePath, alias);
        } catch (GeneralSecurityException | IOException | OperatorCreationException e) {
            throw new IllegalStateException("Failed to generate self-signed TLS certificate", e);
        }
    }

    /**
     * Generates a new RSA key pair of size {@value #KEY_SIZE}.
     *
     * @return the generated RSA key pair
     * @throws NoSuchAlgorithmException if RSA is not available in this JVM
     */
    private static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Builds and signs a self-signed X.509 certificate (subject equals issuer) for the given
     * common name and key pair, valid for the given duration starting now, with a DNS subject
     * alternative name matching the common name, signed using {@value #SIGNATURE_ALGORITHM}.
     *
     * @param commonName the certificate's subject/issuer common name
     * @param keyPair    the key pair whose public key is embedded and whose private key signs the certificate
     * @param validity   how long the certificate remains valid, starting from now
     * @return the signed self-signed {@link X509Certificate}
     * @throws GeneralSecurityException   if certificate conversion fails
     * @throws OperatorCreationException  if the content signer cannot be created
     * @throws CertIOException            if the subject alternative name extension cannot be added
     */
    private static X509Certificate signCertificate(String commonName, KeyPair keyPair, Duration validity)
            throws GeneralSecurityException, OperatorCreationException, CertIOException {
        Instant notBefore = Instant.now();
        Instant notAfter = notBefore.plus(validity);
        X500Name subject = new X500Name("CN=" + commonName);
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());

        var certificateBuilder = new JcaX509v3CertificateBuilder(subject, serialNumber, Date.from(notBefore),
                Date.from(notAfter), subject, keyPair.getPublic());
        certificateBuilder.addExtension(Extension.subjectAlternativeName, false,
                new GeneralNames(new GeneralName(GeneralName.dNSName, commonName)));

        ContentSigner signer = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).build(keyPair.getPrivate());
        X509CertificateHolder certificateHolder = certificateBuilder.build(signer);
        return new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider())
                .getCertificate(certificateHolder);
    }

    /**
     * Writes the given key pair and certificate as a new key entry into a freshly created,
     * temporary PKCS#12 keystore file.
     *
     * @param alias            the alias under which to store the key entry
     * @param keystorePassword the password protecting the keystore and the key entry
     * @param keyPair          the key pair whose private key is stored
     * @param certificate      the certificate chain entry (single self-signed certificate) associated with the key
     * @return the path to the newly created temporary keystore file
     * @throws GeneralSecurityException if the keystore cannot be created or the entry cannot be set
     * @throws IOException              if the keystore file cannot be created or written
     */
    private static Path writeKeystore(String alias, char[] keystorePassword, KeyPair keyPair,
            X509Certificate certificate) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(null, null);
        keyStore.setKeyEntry(alias, keyPair.getPrivate(), keystorePassword, new Certificate[] {certificate});

        Path keystorePath = Files.createTempFile("ledgerx-tls-", ".p12");
        try (OutputStream out = Files.newOutputStream(keystorePath)) {
            keyStore.store(out, keystorePassword);
        }
        return keystorePath;
    }

    /**
     * Describes a generated PKCS#12 keystore file.
     *
     * @param path  the filesystem path to the generated keystore file
     * @param alias the alias under which the key entry is stored
     */
    public record GeneratedKeystore(Path path, String alias) {
    }
}
