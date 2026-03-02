package de.ait.g_75_shop.domain.enums;

/**
 * User roles enumeration for authorization
 * Used by Spring Security for role-based access control
 *
 * Перечисление ролей пользователей для авторизации
 * Используется Spring Security для контроля доступа на основе ролей
 */
public enum Role {

    /**
     * Administrator role - full system access
     * Роль администратора - полный доступ к системе
     */
    ROLE_ADMIN,
    /**
     * Regular user role - limited access
     * Роль обычного пользователя - ограниченный доступ
     */
    ROLE_USER
}
