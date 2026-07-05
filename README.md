<div align="center">
  <img src="logo_campuscare.png" width="110" alt="Logótipo Campus Care">

  # Campus Care

  Aplicação Android para registar e acompanhar pedidos/ocorrências num campus académico.

  Computação Móvel - Engenharia Informática - IPVC
</div>

---

## O que é

A **Campus Care** é uma app Android (nativa, Kotlin + Jetpack Compose) onde:

- um **Utilizador** cria pedidos (categoria, título, localização, descrição e foto opcional), acompanha o estado de cada um, vê o histórico e pode cancelar pedidos ainda não concluídos;
- um **Administrador** vê todos os pedidos, altera-lhes o estado, elimina-os, gere categorias e tem um painel com estatísticas.

Tudo funciona **localmente no dispositivo** (sem internet nem servidor).

## Funcionalidades

**Obrigatórias:** criar conta, login/logout, editar perfil, persistência de dados, navegação, formulários com validação, controlo de acesso por perfil, nome + logótipo próprios.

**Valorização:** pesquisa, filtro por estado, ordenação por data, estatísticas, **modo escuro**, **PT/EN**, **notificações** dentro da app, **exportação para CSV** e adaptação a portrait/landscape.

## Tecnologias

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Room** (SQLite) para persistência
- **DataStore** para preferências (sessão, tema e idioma)
- **Navigation Compose**, **ViewModel**, **Coroutines/Flow**
- **Coil** para carregar as fotografias

## Como executar

**Requisitos:** [Android Studio](https://developer.android.com/studio) recente (Koala ou mais novo) com o Android SDK. O JDK 17 já vem incluído no Android Studio.

1. **Abrir o projeto:** Android Studio -> **File -> Open** -> selecionar a pasta `Campus-Care`. Deixar o **Gradle sincronizar** (a primeira sincronização descarrega dependências e pode demorar alguns minutos).
2. **Criar um emulador:** **Tools -> Device Manager -> Create Virtual Device** -> **Pixel 7** -> imagem de sistema **API 34 (Android 14)** -> *Finish*.
   - *(Alternativa: ligar um telemóvel Android real com a depuração USB ativa.)*
3. **Correr:** selecionar o emulador na barra superior e carregar em **Run**.

> Requisitos de SDK: `minSdk 24`, `compileSdk 34`, `targetSdk 34`.

## Estrutura do projeto

```
app/src/main/java/pt/ipvc/csm/
├── data/            # Room (entidades, DAOs, base de dados), repositório, sessão (DataStore)
├── model/           # enums (Role, RequestStatus)
├── ui/
│   ├── theme/       # cores, tipografia, tema (claro/escuro)
│   ├── components/  # componentes reutilizáveis (cartões, chips, campos...)
│   ├── navigation/  # NavHost e rotas
│   └── screens/     # ecrãs (auth, user, admin)
├── viewmodel/       # AuthViewModel, UserViewModel, AdminViewModel
└── util/            # datas, fotografias, exportação CSV
res/values/          # strings PT (+ res/values-en para EN)
```
