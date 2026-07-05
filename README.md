<div align="center">
  <img src="logo_campuscare.png" width="110" alt="Logótipo Campus Care">

  # Campus Care

  Aplicação Android para registar e acompanhar pedidos/ocorrências num campus académico.

  Computação Móvel · Engenharia Informática · Época de Recurso · IPVC
</div>

---

## O que é

A **Campus Care** é uma app Android (nativa, Kotlin + Jetpack Compose) onde:

- um **Utilizador** cria pedidos (categoria, título, localização, descrição e foto opcional), acompanha o estado de cada um, vê o histórico e pode cancelar pedidos ainda não concluídos;
- um **Administrador** vê todos os pedidos, altera-lhes o estado, elimina-os, gere categorias e tem um painel com estatísticas.

Tudo funciona **localmente no dispositivo** (sem internet nem servidor).

## Funcionalidades

**Obrigatórias:** criar conta, login/logout, editar perfil, persistência de dados, navegação, formulários com validação, controlo de acesso por perfil, nome + logótipo próprios.

**Valorização:** pesquisa, filtro por estado, ordenação por data, estatísticas, **modo escuro**, **PT/EN** (troca em tempo real), **notificações** dentro da app, **exportação para CSV** (via share sheet) e adaptação a portrait/landscape.

## Tecnologias

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Room** (SQLite) para persistência
- **DataStore** para preferências (sessão, tema, idioma)
- **Navigation Compose**, **ViewModel**, **Coroutines/Flow** (arquitetura MVVM)
- **Coil** para carregar as fotografias

## Como executar

**Requisitos:** [Android Studio](https://developer.android.com/studio) recente (Koala ou mais novo) com o Android SDK. O JDK 17 já vem incluído no Android Studio.

1. **Abrir o projeto:** Android Studio → **File → Open** → selecionar a pasta `CSM_Mobile`. Deixar o **Gradle sincronizar** (a primeira sincronização descarrega dependências e pode demorar alguns minutos).
2. **Criar um emulador:** **Tools → Device Manager → Create Virtual Device** → **Pixel 7** → imagem de sistema **API 34 (Android 14)** → *Finish*.
   - *(Alternativa: ligar um telemóvel Android real com a depuração USB ativa.)*
3. **Correr:** selecionar o emulador na barra superior e carregar em **Run ▶**. A app arranca no ecrã de login.

> Requisitos de SDK: `minSdk 24`, `compileSdk 34`, `targetSdk 34`.

## Primeiro arranque (base de dados vazia)

A app começa **sem dados**, por isso, para experimentar o fluxo completo:

1. **Criar conta** → escolher **Administrador**.
2. No separador **Categorias**, criar algumas (ex.: Manutenção, Limpeza, Segurança).
3. **Terminar sessão** → **Criar conta** → escolher **Utilizador**.
4. Como utilizador, criar um **Novo pedido** (categoria, título, localização, descrição, foto opcional).
5. **Terminar sessão** → entrar como **admin** → abrir o pedido → **alterar o estado**.
6. Voltar a entrar como **utilizador** → ver a **notificação** e a **linha do tempo** atualizadas.

> **Fotografias no emulador:** arrasta uma imagem para a janela do emulador, ou usa a app *Câmara* do emulador para tirar uma foto — depois aparece no seletor de imagens.

## Persistência dos dados

Base de dados **SQLite local** através do **Room**; os dados ficam num ficheiro (`csm.db`) na zona privada da app. As preferências (sessão, modo escuro, idioma) usam **DataStore**. As fotos são copiadas para o armazenamento interno da app. As passwords são guardadas com **hash SHA-256** (nunca em texto simples).

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

## Autor

**Pedro Freitas** — nº 27021 · Computação Móvel (Época de Recurso), IPVC
