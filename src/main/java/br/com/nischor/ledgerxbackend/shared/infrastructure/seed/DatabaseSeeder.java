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
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Creates a new seeder wired with the repositories and password encoder needed to persist
     * generated sample data across every bounded context.
     *
     * @param companyRepository          repository used to persist generated companies
     * @param userRepository             repository used to persist generated users
     * @param financialAccountRepository repository used to persist generated financial accounts
     * @param categoryRepository         repository used to persist generated categories
     * @param partyRepository            repository used to persist generated parties
     * @param invoiceRepository          repository used to persist generated invoices
     * @param transactionRepository      repository used to persist generated transactions
     * @param passwordEncoder            encoder used to hash the sample users' passwords
     */
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

    /**
     * Entry point invoked by Spring Boot at startup. If the database already has at least one
     * company, seeding is skipped; otherwise generates companies, users, financial accounts,
     * categories, parties, invoices, and transactions, in that dependency order, all within a
     * single transaction.
     *
     * @param args the application arguments (unused)
     */
    @Override
    @Transactional
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

    /**
     * Generates and persists {@value #COMPANY_COUNT} sample companies with randomized pt-BR
     * addresses, names and CNPJ document numbers.
     *
     * @return the list of persisted companies
     */
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

    /**
     * Generates and persists {@value #USER_COUNT} sample users with randomized names, e-mails,
     * hashed passwords, and (for roughly half of them) a randomly assigned role.
     */
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

    /**
     * Generates and persists {@value #FINANCIAL_ACCOUNT_COUNT} sample financial accounts,
     * each assigned to a randomly chosen company with a randomized opening balance.
     *
     * @param companies the companies to randomly assign generated accounts to
     * @return the persisted financial accounts grouped by owning company id
     */
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

    /**
     * Generates and persists {@value #CATEGORY_COUNT} sample income/expense categories
     * (roughly one third income, two thirds expense), each assigned to a randomly chosen company.
     *
     * @param companies the companies to randomly assign generated categories to
     * @return the persisted categories grouped by owning company id
     */
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

    /**
     * Generates and persists {@value #PARTY_COUNT} sample parties (customers/suppliers), each
     * assigned to a randomly chosen company, randomly individual (CPF) or company (CNPJ), with
     * a randomized name, document number, e-mail, and party type.
     *
     * @param companies the companies to randomly assign generated parties to
     * @return the persisted parties grouped by owning company id
     */
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

    /**
     * Generates and persists {@value #INVOICE_COUNT} sample invoices, each assigned to a randomly
     * chosen company and one of its previously generated parties, with 1 to 3 randomly amounted
     * monthly installments starting from a randomized future due date. Companies with no
     * generated parties are skipped for a given iteration.
     *
     * @param companies        the companies to randomly assign generated invoices to
     * @param partiesByCompany the previously generated parties, grouped by owning company id
     */
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

    /**
     * Generates and persists {@value #TRANSACTION_COUNT} sample transactions, each assigned to a
     * randomly chosen company's financial account and category, applying the corresponding debit
     * or credit to the account's balance. If a randomly chosen expense would overdraw the account,
     * the transaction is switched to an income category from the same company when one is
     * available, or skipped otherwise. All affected financial accounts are saved again at the end
     * to persist their updated balances.
     *
     * @param accountsByCompany   the previously generated financial accounts, grouped by owning company id
     * @param categoriesByCompany the previously generated categories, grouped by owning company id
     */
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
                // movement always agree; if none exists, skip this transaction rather than risk
                // an unsafe clamp when the balance is at or near zero.
                var incomeCategory = categories.stream()
                        .filter(candidate -> candidate.getType() == TransactionType.INCOME)
                        .findAny();
                if (incomeCategory.isEmpty()) {
                    continue;
                }
                category = incomeCategory.get();
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

    /**
     * Picks a random Brazilian state abbreviation.
     *
     * @return a randomly chosen two-letter Brazilian state code
     */
    private String randomState() {
        return randomFrom(BRAZILIAN_STATES);
    }

    /**
     * Generates a random Brazilian ZIP code (CEP) in the {@code NNNNN-NNN} format.
     *
     * @return a randomly generated CEP-formatted string
     */
    private String randomZipCode() {
        return "%05d-%03d".formatted(faker.number().numberBetween(1000, 99999), faker.number().numberBetween(0, 999));
    }

    /**
     * Generates a random decimal amount within the given inclusive-ish range, scaled to two
     * decimal places using {@link RoundingMode#HALF_EVEN}.
     *
     * @param min the lower bound of the range
     * @param max the upper bound of the range
     * @return a random amount between {@code min} and {@code max}
     */
    private BigDecimal randomAmount(long min, long max) {
        double value = min + random.nextDouble() * (max - min);
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Picks a random element from the given list.
     *
     * @param values the list to pick from
     * @param <T>    the element type
     * @return a randomly chosen element of {@code values}
     */
    private <T> T randomFrom(List<T> values) {
        return values.get(random.nextInt(values.size()));
    }

    /**
     * Picks a random element from the given array.
     *
     * @param values the array to pick from
     * @param <T>    the element type
     * @return a randomly chosen element of {@code values}
     */
    private <T> T randomFrom(T[] values) {
        return values[random.nextInt(values.length)];
    }

    /**
     * Picks a random value among the given enum constants.
     *
     * @param values the enum constants to pick from
     * @param <T>    the enum type
     * @return a randomly chosen constant from {@code values}
     */
    @SafeVarargs
    private <T extends Enum<T>> T randomEnum(T... values) {
        return values[random.nextInt(values.length)];
    }

    /**
     * Generates a random, check-digit-valid CPF (11 digits).
     *
     * @return a randomly generated, structurally valid CPF number
     */
    private String randomCpf() {
        int[] base = random.ints(9, 0, 10).toArray();
        int firstCheckDigit = weightedCheckDigit(base, 10);
        int[] withFirst = appendDigit(base, firstCheckDigit);
        int secondCheckDigit = weightedCheckDigit(withFirst, 11);
        return digitsToString(appendDigit(withFirst, secondCheckDigit));
    }

    /**
     * Generates a random, check-digit-valid CNPJ (14 digits).
     *
     * @return a randomly generated, structurally valid CNPJ number
     */
    private String randomCnpj() {
        int[] base = random.ints(12, 0, 10).toArray();
        int firstCheckDigit = weightedCheckDigit(base, new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int[] withFirst = appendDigit(base, firstCheckDigit);
        int secondCheckDigit = weightedCheckDigit(withFirst, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        return digitsToString(appendDigit(withFirst, secondCheckDigit));
    }

    /**
     * Computes a check digit using the CPF-style weighting scheme, where weights start at
     * {@code startingWeight} and decrease by one for each subsequent digit.
     *
     * @param digits         the digits to compute the check digit over
     * @param startingWeight the weight applied to the first digit
     * @return the computed check digit (0-9)
     */
    private static int weightedCheckDigit(int[] digits, int startingWeight) {
        int sum = 0;
        int weight = startingWeight;
        for (int digit : digits) {
            sum += digit * weight--;
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    /**
     * Computes a check digit using the CNPJ-style weighting scheme, where each digit is
     * multiplied by its corresponding entry in {@code weights}.
     *
     * @param digits  the digits to compute the check digit over
     * @param weights the per-digit weights, must be the same length as {@code digits}
     * @return the computed check digit (0-9)
     */
    private static int weightedCheckDigit(int[] digits, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += digits[i] * weights[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    /**
     * Returns a new array containing all of {@code digits} followed by {@code digit}.
     *
     * @param digits the original digits
     * @param digit  the digit to append
     * @return a new array with {@code digit} appended
     */
    private static int[] appendDigit(int[] digits, int digit) {
        int[] result = new int[digits.length + 1];
        System.arraycopy(digits, 0, result, 0, digits.length);
        result[digits.length] = digit;
        return result;
    }

    /**
     * Concatenates the given digits into their decimal string representation.
     *
     * @param digits the digits to concatenate
     * @return the digits joined together as a string
     */
    private static String digitsToString(int[] digits) {
        var builder = new StringBuilder(digits.length);
        for (int digit : digits) {
            builder.append(digit);
        }
        return builder.toString();
    }
}
