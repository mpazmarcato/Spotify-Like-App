```bash
SpotifyLikeApp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controller/            # Controladores da interface JavaFX
│   │   │   ├── model/                 # Classes modelo para usuários, músicas, playlists
│   │   │   ├── utils/                 # Classes auxiliares 
│   │   │   ├── media/                 # Arquivos de música ou mídia
│   │   │   ├── servicies/             # Serivces
│   │   │   ├── repositories/          # repositories
│   │   │   └── MainApp.java           # Classe principal
│   │   └── resources/
│   │       ├── com.example.demo/      # Arquivos FXML para layouts JavaFX
│   │       └── hibernate.cfg.xml      # Configuração do Hibernate para conexão com o banco
│   └── test/                          # Testes do projeto
│       └── repositories/              # Testes dos repositórios
│           └── integration/           # Testes de integração
└── README.md                          # Documentação do projeto
```

### Projetos

O projeto foi desenvolvido em Java com a utilização do JavaFX para a interface gráfica. Implementado no padrão de projeto
MVC (Model-View-Controller) para separação de responsabilidades. Utilizamos testes de software no repository, pois onde continha a maior parte da lógica
da nossa aplicação.

No projeto foram usados os seguintes assuntos de Programção Orientada a Objetos: Herança, Polimorfismo, Encapsulamento, Abstração, Interfaces, Classes Abstratas.

### Repositories

Usamos o padrão de projeto Repository para abstrair a camada de persistência de dados. 
Com o hibernate, conseguimos mapear as entidades do banco de dados e realizar operações de CRUD.
E utilizamos o banco de dados H2 para armazenar os dados em memória por fins de praticidade.


### Services
No services, temos a camada de negócio da aplicação. Aqui é onde implementamos a lógica de negócio da aplicação.
Também por fins didáticos fizemos um service de maneira simples.

### Controller
Os controllers da nossa aplicação fazem a interação com o usuário, recebendo os dados e enviando pelo javaFX.

### Como rodar o projeto pelo terminal:

```bash
#ensinar a rodar
```