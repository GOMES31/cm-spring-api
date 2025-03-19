# Spring API com Autenticação JWT

Esta API Spring Boot utiliza **JWT (JSON Web Token)** para autenticação e **MySQL 8** como base de dados. A aplicação mostra como implementar autenticação segura com tokens, utilizando uma abordagem **stateless**.

### Tecnologias Utilizadas:
- **Spring Boot 3.4.3**
- **Spring Security** (para autenticação JWT)
- **MySQL 8** (para gestão da base de dados)
- **JJWT** (para criação e verificação de tokens JWT)

---

## Exemplos de Requisições POST

### 1. Registo de Novo Utilizador

**URL**: `http://localhost:8080/auth/register`

**Body (JSON)**:
```json
{
  "name": "John Doe",
  "email": "johndoe@example.com",
  "password": "password123"
}
