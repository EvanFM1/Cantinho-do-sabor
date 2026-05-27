package org.example.util;

import org.example.model.entity.UsuarioEntity;

public class Session {
    private static UsuarioEntity usuarioLogado;

    public static void setUsuario(UsuarioEntity usuario) {
        usuarioLogado = usuario;
    }
    public static UsuarioEntity getUsuario() {
        return usuarioLogado;
    }
    public static void logout() {
        usuarioLogado = null;
    }
}