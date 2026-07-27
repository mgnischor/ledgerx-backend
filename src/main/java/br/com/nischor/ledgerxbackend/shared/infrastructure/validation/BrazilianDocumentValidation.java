package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

/**
 * Shared check-digit validation algorithms for Brazilian CPF and CNPJ document numbers, used by
 * {@link CpfValidator}, {@link CnpjValidator}, and {@link PartyDocumentValidator}.
 */
final class BrazilianDocumentValidation {

    private static final int[] CNPJ_FIRST_DIGIT_WEIGHTS = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] CNPJ_SECOND_DIGIT_WEIGHTS = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    /**
     * Prevents instantiation; this class only exposes static validation methods.
     */
    private BrazilianDocumentValidation() {
    }

    /**
     * Validates a CPF number: strips non-digit characters, rejects values that are not 11 digits
     * long or consist of a single repeated digit (a common invalid pattern, e.g. "11111111111"),
     * and verifies both check digits using the standard CPF weighting algorithm.
     *
     * @param value the raw CPF value, digits only or formatted
     * @return {@code true} if {@code value} is a structurally valid CPF, {@code false} otherwise
     */
    static boolean isValidCpf(String value) {
        var digits = value.replaceAll("\\D", "");
        if (digits.length() != 11 || digits.chars().distinct().count() == 1) {
            return false;
        }

        int firstCheckDigit = checkDigit(digits.substring(0, 9), 10);
        int secondCheckDigit = checkDigit(digits.substring(0, 9) + firstCheckDigit, 11);

        return digits.equals(digits.substring(0, 9) + firstCheckDigit + secondCheckDigit);
    }

    /**
     * Validates a CNPJ number: strips non-digit characters, rejects values that are not 14 digits
     * long or consist of a single repeated digit, and verifies both check digits using the
     * standard CNPJ weighting algorithm.
     *
     * @param value the raw CNPJ value, digits only or formatted
     * @return {@code true} if {@code value} is a structurally valid CNPJ, {@code false} otherwise
     */
    static boolean isValidCnpj(String value) {
        var digits = value.replaceAll("\\D", "");
        if (digits.length() != 14 || digits.chars().distinct().count() == 1) {
            return false;
        }

        int firstCheckDigit = weightedCheckDigit(digits.substring(0, 12), CNPJ_FIRST_DIGIT_WEIGHTS);
        int secondCheckDigit = weightedCheckDigit(digits.substring(0, 12) + firstCheckDigit,
                CNPJ_SECOND_DIGIT_WEIGHTS);

        return digits.equals(digits.substring(0, 12) + firstCheckDigit + secondCheckDigit);
    }

    /**
     * Computes a CPF-style check digit: multiplies each digit by a weight starting at
     * {@code startingWeight} and decreasing by one per digit, then reduces the sum modulo 11.
     *
     * @param base           the digits to compute the check digit over
     * @param startingWeight the weight applied to the first digit
     * @return the computed check digit (0-9)
     */
    private static int checkDigit(String base, int startingWeight) {
        int sum = 0;
        int weight = startingWeight;
        for (int i = 0; i < base.length(); i++) {
            sum += Character.digit(base.charAt(i), 10) * weight--;
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    /**
     * Computes a CNPJ-style check digit: multiplies each digit by its corresponding entry in
     * {@code weights}, then reduces the sum modulo 11.
     *
     * @param base    the digits to compute the check digit over
     * @param weights the per-digit weights, must be at least as long as {@code base}
     * @return the computed check digit (0-9)
     */
    private static int weightedCheckDigit(String base, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += Character.digit(base.charAt(i), 10) * weights[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
