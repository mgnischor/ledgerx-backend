package br.com.nischor.ledgerxbackend.shared.application;

/**
 * Generic contract for an application use case in the hexagonal architecture.
 *
 * <p>Implementations encapsulate a single application-level operation, receiving
 * an input and producing an output, keeping application logic decoupled from
 * inbound adapters (controllers, messaging listeners, etc.).
 *
 * @param <INPUT>  the type of the input required to execute the use case
 * @param <OUTPUT> the type of the result produced by the use case
 */
public interface UseCase<INPUT, OUTPUT> {

    /**
     * Executes the use case with the given input.
     *
     * @param input the input data required to perform the operation
     * @return the result produced by executing the use case
     */
    OUTPUT execute(INPUT input);
}
