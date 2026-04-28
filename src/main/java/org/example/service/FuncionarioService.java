package org.example.service;

import jakarta.persistence.*;
import org.example.entity.FuncionarioEntity;
import org.example.repository.FuncionarioRepository;
import java.util.List;

public class FuncionarioService {
    private EntityManager entityManager;
    private FuncionarioRepository funcionarioRepository;

    public FuncionarioService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.funcionarioRepository = new FuncionarioRepository(entityManager);
    }

    public FuncionarioEntity criarFuncionario(FuncionarioEntity funcionario) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            if (funcionario.getNome() == null || funcionario.getNome().isEmpty()) {
                throw new RuntimeException("Nome do funcionário é obrigatório!");
            }
            if (funcionario.getCargo() == null || funcionario.getCargo().isEmpty()) {
                throw new RuntimeException("Cargo é obrigatório!");
            }
            if (funcionario.getTelefone() == null || funcionario.getTelefone().isEmpty()) {
                throw new RuntimeException("Telefone de contato é obrigatório!");
            }

            funcionarioRepository.salvar(funcionario);
            transaction.commit();
            return funcionario;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public FuncionarioEntity buscarPorId(Long id) {
        return funcionarioRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado!"));
    }

    public List<FuncionarioEntity> listarFuncionarios() {
        return funcionarioRepository.listarTodos();
    }

    public FuncionarioEntity atualizarFuncionario(Long id, FuncionarioEntity dadosAtualizados) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            FuncionarioEntity funcionario = funcionarioRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado!"));

            funcionario.setNome(dadosAtualizados.getNome());
            funcionario.setCargo(dadosAtualizados.getCargo());
            funcionario.setTelefone(dadosAtualizados.getTelefone());

            FuncionarioEntity atualizado = funcionarioRepository.salvar(funcionario);

            transaction.commit();
            return atualizado;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void deletarFuncionario(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            FuncionarioEntity funcionario = funcionarioRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado!"));

            funcionarioRepository.deletar(funcionario);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}