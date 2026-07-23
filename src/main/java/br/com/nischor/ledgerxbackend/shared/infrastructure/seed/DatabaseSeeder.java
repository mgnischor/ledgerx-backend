package br.com.nischor.ledgerxbackend.shared.infrastructure.seed;

import br.com.nischor.ledgerxbackend.accounting.domain.model.Category;
import br.com.nischor.ledgerxbackend.accounting.domain.model.FinancialAccount;
import br.com.nischor.ledgerxbackend.accounting.domain.model.Transaction;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.CategoryRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.FinancialAccountRepository;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.TransactionRepository;
import br.com.nischor.ledgerxbackend.billing.domain.model.Installment;
import br.com.nischor.ledgerxbackend.billing.domain.model.Invoice;
import br.com.nischor.ledgerxbackend.billing.domain.model.Party;
import br.com.nischor.ledgerxbackend.billing.domain.model.PartyType;
import br.com.nischor.ledgerxbackend.billing.domain.repository.InvoiceRepository;
import br.com.nischor.ledgerxbackend.billing.domain.repository.PartyRepository;
import br.com.nischor.ledgerxbackend.company.domain.model.Company;
import br.com.nischor.ledgerxbackend.company.domain.model.CompanySize;
import br.com.nischor.ledgerxbackend.company.domain.repository.CompanyRepository;
import br.com.nischor.ledgerxbackend.company.domain.valueobject.Address;
import br.com.nischor.ledgerxbackend.identity.domain.model.Role;
import br.com.nischor.ledgerxbackend.identity.domain.model.User;
import br.com.nischor.ledgerxbackend.identity.domain.repository.UserRepository;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.EmailAddress;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.Money;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Populates the database with realistic pt-BR sample data the first time the application starts
 * against an empty database (guarded by {@code companyRepository.count() == 0}). Generates
 * roughly 5000 records in total, spread across every bounded context, using
 * <a href="https://www.datafaker.net/">Datafaker</a> with the {@code pt_BR} locale.
 *
 * <p>Disable with {@code ledgerx.seed.enabled=false} (e.g. in production).
 */
@Component
@ConditionalOnProperty(value = "ledgerx.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private static final int COMPANY_COUNT = 15;
    private static final int USER_COUNT = 150;
    private static final int FINANCIAL_ACCOUNT_COUNT = 60;
    private static final int CATEGORY_COUNT = 90;
    private static final int PARTY_COUNT = 600;
    private static final int INVOICE_COUNT = 600;
    private static final int TRANSACTION_COUNT = 3485;

    private static final String[] BRAZILIAN_STATES = {
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE",
            "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};

    private static final String[] INCOME_CATEGORY_NAMES = {"Vendas", "Prestação de Serviços", "Comissões",
            "Juros Recebidos", "Outras Receitas"};
    private static final String[] EXPENSE_CATEGORY_NAMES = {"Fornecedores", "Aluguel", "Salários", "Impostos",
            "Marketing", "Transporte", "Alimentação", "Energia Elétrica", "Internet e Telefonia", "Manutenção"};

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final FinancialAccountRepository financialAccountRepository;
    private final CategoryRepository categoryRepository;
    private final PartyRepository partyRepository;
    private final InvoiceRepository invoiceRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker(Locale.of("pt", "BR"));
    private final Random random = new Random();

    public DatabaseSeeder(CompanyRepository companyRepository, UserRepository userRepository,
            FinancialAccountRepository financialAccountRepository, CategoryRepository categoryRepository,
            PartyRepository partyRepository, InvoiceRepository invoiceRepository,
            TransactionRepository transactionRepository, PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.financialAccountRepository = financialAccountRepository;
        this.categoryRepository = categoryRepository;
        this.partyRepository = partyRepository;
        this.invoiceRepository = invoiceRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (companyRepository.count() > 0) {
            log.info("Database already contains data, skipping seed");
            return;
        }

        log.info("Seeding database with sample pt-BR data (first startup)...");

        var companies = seedCompanies();
        seedUsers();
        var accountsByCompany = seedFinancialAccounts(companies);
        var categoriesByCompany = seedCategories(companies);
        var partiesByCompany = seedParties(companies);
        seedInvoices(companies, partiesByCompany);
        seedTransactions(accountsByCompany, categoriesByCompany);

        log.info("Database seed completed: {} companies, {} users, {} financial accounts, {} categories, "
                        + "{} parties, {} invoices, {} transactions",
                COMPANY_COUNT, USER_COUNT, FINANCIAL_ACCOUNT_COUNT, CATEGORY_COUNT, PARTY_COUNT, INVOICE_COUNT,
                TRANSACTION_COUNT);
    }

    private List<Company> seedCompanies() {
        List<Company> companies = new ArrayList<>();
        for (int i = 0; i < COMPANY_COUNT; i++) {
            var address = new Address(faker.address().streetName(), String.valueOf(faker.number().numberBetween(1, 9999)),
                    faker.address().city(), randomState(), randomZipCode(), "Brazil");
            var company = new Company(UUID.randomUUID(), faker.company().name() + " LTDA", faker.company().name(),
                    DocumentNumber.cnpj(randomCnpj()), randomEnum(CompanySize.values()), address);
            companies.add(companyRepository.save(company));
        }
        return companies;
    }

    private void seedUsers() {
        for (int i = 0; i < USER_COUNT; i++) {
            var email = new EmailAddress(faker.internet().emailAddress(faker.name().username() + i));
            var user = new User(UUID.randomUUID(), faker.name().fullName(), email,
                    passwordEncoder.encode("Seed@" + faker.number().digits(6)));
            if (random.nextBoolean()) {
                user.grant(randomEnum(Role.values()));
            }
            userRepository.save(user);
        }
    }

    private Map<UUID, List<FinancialAccount>> seedFinancialAccounts(List<Company> companies) {
        Map<UUID, List<FinancialAccount>> accountsByCompany = new java.util.HashMap<>();
        for (int i = 0; i < FINANCIAL_ACCOUNT_COUNT; i++) {
            var company = randomFrom(companies);
            var openingBalance = Money.brl(randomAmount(50_000, 500_000));
            var account = new FinancialAccount(UUID.randomUUID(), company.getId(),
                    "Conta " + faker.company().suffix() + " " + faker.number().digits(4), openingBalance);
            var saved = financialAccountRepository.save(account);
            accountsByCompany.computeIfAbsent(company.getId(), key -> new ArrayList<>()).add(saved);
        }
        return accountsByCompany;
    }

    private Map<UUID, List<Category>> seedCategories(List<Company> companies) {
        Map<UUID, List<Category>> categoriesByCompany = new java.util.HashMap<>();
        for (int i = 0; i < CATEGORY_COUNT; i++) {
            var company = randomFrom(companies);
            boolean income = random.nextInt(3) == 0;
            var name = income ? randomFrom(INCOME_CATEGORY_NAMES) : randomFrom(EXPENSE_CATEGORY_NAMES);
            var category = new Category(UUID.randomUUID(), company.getId(), name,
                    income ? TransactionType.INCOME : TransactionType.EXPENSE);
            var saved = categoryRepository.save(category);
            categoriesByCompany.computeIfAbsent(company.getId(), key -> new ArrayList<>()).add(saved);
        }
        return categoriesByCompany;
    }

    private Map<UUID, List<Party>> seedParties(List<Company> companies) {
        Map<UUID, List<Party>> partiesByCompany = new java.util.HashMap<>();
        for (int i = 0; i < PARTY_COUNT; i++) {
            var company = randomFrom(companies);
            var type = randomEnum(PartyType.values());
            boolean isIndividual = random.nextBoolean();
            var name = isIndividual ? faker.name().fullName() : faker.company().name();
            var document = isIndividual ? DocumentNumber.cpf(randomCpf()) : DocumentNumber.cnpj(randomCnpj());
            var email = new EmailAddress(faker.internet().emailAddress(faker.name().username() + "party" + i));
            var party = new Party(UUID.randomUUID(), company.getId(), name, document, email, type);
            var saved = partyRepository.save(party);
            partiesByCompany.computeIfAbsent(company.getId(), key -> new ArrayList<>()).add(saved);
        }
        return partiesByCompany;
    }

    private void seedInvoices(List<Company> companies, Map<UUID, List<Party>> partiesByCompany) {
        for (int i = 0; i < INVOICE_COUNT; i++) {
            var company = randomFrom(companies);
            var parties = partiesByCompany.get(company.getId());
            if (parties == null || parties.isEmpty()) {
                continue;
            }

            var party = randomFrom(parties);
            var firstDueDate = LocalDate.now(ZoneOffset.UTC).plusDays(faker.number().numberBetween(1, 60));
            int installmentCount = faker.number().numberBetween(1, 4);

            List<Installment> installments = new ArrayList<>();
            for (int n = 1; n <= installmentCount; n++) {
                installments.add(new Installment(UUID.randomUUID(), n, Money.brl(randomAmount(50, 5_000)),
                        firstDueDate.plusMonths(n - 1)));
            }

            var invoice = new Invoice(UUID.randomUUID(), company.getId(), party.getId(), party.getType(),
                    installments);
            invoiceRepository.save(invoice);
        }
    }

    private void seedTransactions(Map<UUID, List<FinancialAccount>> accountsByCompany,
            Map<UUID, List<Category>> categoriesByCompany) {
        var companyIds = new ArrayList<>(accountsByCompany.keySet());

        for (int i = 0; i < TRANSACTION_COUNT; i++) {
            var companyId = randomFrom(companyIds);
            var accounts = accountsByCompany.get(companyId);
            var categories = categoriesByCompany.get(companyId);
            if (accounts == null || accounts.isEmpty() || categories == null || categories.isEmpty()) {
                continue;
            }

            var account = randomFrom(accounts);
            var category = randomFrom(categories);
            var amount = Money.brl(randomAmount(10, 10_000));

            if (category.getType() == TransactionType.EXPENSE
                    && amount.amount().compareTo(account.getBalance().amount()) > 0) {
                // Not enough balance for this random expense: prefer switching to an income
                // category from the same company so the transaction type and the actual balance
                // movement always agree; if none exists, fall back to a small, affordable amount.
                var incomeCategory = categories.stream()
                        .filter(candidate -> candidate.getType() == TransactionType.INCOME)
                        .findAny();
                if (incomeCategory.isPresent()) {
                    category = incomeCategory.get();
                } else {
                    amount = Money.brl(account.getBalance().amount()
                            .multiply(BigDecimal.valueOf(0.5))
                            .max(BigDecimal.valueOf(0.01))
                            .setScale(2, RoundingMode.HALF_EVEN));
                }
            }

            if (category.getType() == TransactionType.EXPENSE) {
                account.debit(amount);
            } else {
                account.credit(amount);
            }

            var occurredOn = LocalDate.now(ZoneOffset.UTC).minusDays(faker.number().numberBetween(0, 4 * 365));
            var transaction = new Transaction(UUID.randomUUID(), account.getId(), category.getId(),
                    category.getType(), amount, faker.commerce().productName(), occurredOn);
            transactionRepository.save(transaction);
        }

        accountsByCompany.values().stream().flatMap(List::stream).forEach(financialAccountRepository::save);
    }

    private String randomState() {
        return randomFrom(BRAZILIAN_STATES);
    }

    private String randomZipCode() {
        return "%05d-%03d".formatted(faker.number().numberBetween(1000, 99999), faker.number().numberBetween(0, 999));
    }

    private BigDecimal randomAmount(long min, long max) {
        double value = min + random.nextDouble() * (max - min);
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_EVEN);
    }

    private <T> T randomFrom(List<T> values) {
        return values.get(random.nextInt(values.size()));
    }

    private <T> T randomFrom(T[] values) {
        return values[random.nextInt(values.length)];
    }

    @SafeVarargs
    private <T extends Enum<T>> T randomEnum(T... values) {
        return values[random.nextInt(values.length)];
    }

    private String randomCpf() {
        int[] base = random.ints(9, 0, 10).toArray();
        int firstCheckDigit = weightedCheckDigit(base, 10);
        int[] withFirst = appendDigit(base, firstCheckDigit);
        int secondCheckDigit = weightedCheckDigit(withFirst, 11);
        return digitsToString(appendDigit(withFirst, secondCheckDigit));
    }

    private String randomCnpj() {
        int[] base = random.ints(12, 0, 10).toArray();
        int firstCheckDigit = weightedCheckDigit(base, new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int[] withFirst = appendDigit(base, firstCheckDigit);
        int secondCheckDigit = weightedCheckDigit(withFirst, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        return digitsToString(appendDigit(withFirst, secondCheckDigit));
    }

    private static int weightedCheckDigit(int[] digits, int startingWeight) {
        int sum = 0;
        int weight = startingWeight;
        for (int digit : digits) {
            sum += digit * weight--;
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static int weightedCheckDigit(int[] digits, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += digits[i] * weights[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static int[] appendDigit(int[] digits, int digit) {
        int[] result = new int[digits.length + 1];
        System.arraycopy(digits, 0, result, 0, digits.length);
        result[digits.length] = digit;
        return result;
    }

    private static String digitsToString(int[] digits) {
        var builder = new StringBuilder(digits.length);
        for (int digit : digits) {
            builder.append(digit);
        }
        return builder.toString();
    }
}
