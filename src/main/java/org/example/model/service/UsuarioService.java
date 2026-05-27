package org.example.model.service;

import jakarta.persistence.EntityManager;
import org.example.model.entity.UsuarioEntity;
import org.example.model.repository.UsuarioRepository;
import org.example.util.Session;

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EntityManager entityManager;

    public UsuarioService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.usuarioRepository = new UsuarioRepository(entityManager);
    }
    // LOGIN
    public UsuarioEntity login(String login, String senha) {
        UsuarioEntity usuario = usuarioRepository.buscarPorLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
        if (!usuario.getSenha().equals(senha)) {
            throw new RuntimeException("Senha incorreta!");
        }
        Session.setUsuario(usuario);
        return usuario;
    }
    // LOGOUT
    public void logout() {
        Session.logout();
    }
    // É ADMIN?
    public boolean isAdmin() {
        UsuarioEntity usuario = Session.getUsuario();

        if (usuario == null) {
            throw new RuntimeException("Nenhum usuário logado!");
        }
        return "ADMIN".equalsIgnoreCase(usuario.getPerfil());
    }
    // USUÁRIO LOGADO
    public UsuarioEntity usuarioLogado() {
        return Session.getUsuario();
    }
    // ESTÁ LOGADO?
    public boolean estaLogado() {
        return Session.getUsuario() != null;
    }
    // PERFIL ATUAL
    public String getPerfil() {
        UsuarioEntity usuario = Session.getUsuario();

        if (usuario == null) {
            throw new RuntimeException("Nenhum usuário logado!");
        }
        return usuario.getPerfil();
    }
}
