package br.com.nischor.ledgerxbackend.shared.application;

public interface UseCase<INPUT, OUTPUT> {

    OUTPUT execute(INPUT input);
}
