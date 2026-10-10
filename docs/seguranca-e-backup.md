# Remindr — segurança, PIN, proteção de dados e backups

## Dados armazenados

O Remindr mantém os aniversários em um banco SQLite local (`remindr.db`). Cada registro contém nome, data de nascimento e anotações. A tabela `config` armazena valores como o hash do PIN, a preferência de biometria e o estado da introdução inicial. O código de acesso ao banco está em `app/src/main/java/com/vega/remindr/data/RemindrDatabase.kt`.

O banco é mantido no armazenamento privado da aplicação pelo mecanismo padrão do Android. Isso fornece o isolamento normal do sandbox do aplicativo, mas **não significa que o conteúdo do SQLite esteja criptografado por uma camada própria do Remindr**. O código atual usa `SQLiteOpenHelper` e não configura SQLCipher nem outra criptografia de banco de dados. Um dispositivo comprometido, com root, depuração indevida ou acesso a um backup exportado pode contornar as proteções de interface.

## PIN e biometria

- O PIN tem quatro dígitos e o fluxo de criação pede confirmação duas vezes.
- O app guarda o hash SHA-256 do PIN, e não o PIN em texto puro. O hash não tem salt e não usa uma função deliberadamente lenta de derivação de chave; por isso, um PIN de quatro dígitos tem um espaço de busca pequeno se o hash for obtido. Não há limitação de tentativas nem atraso progressivo implementado no verificador atual.
- O bloqueio é uma barreira de interface. Ele não criptografa o banco, os backups nem as anotações.
- A biometria usa `BiometricPrompt` com `BIOMETRIC_STRONG`; a verificação biométrica em si é feita pelo Android. O Remindr armazena somente a preferência de habilitação, não a impressão digital.
- A recuperação/redefinição do PIN depende de autenticação biométrica no fluxo atual. Se a biometria não estiver disponível e o PIN for esquecido, o código não oferece uma recuperação alternativa.

## Exportação e importação manual

A exportação atual gera um arquivo de texto (`text/plain`) com o cabeçalho `REMINDR_BACKUP_V1`. Os campos são codificados em Base64 para representar os dados no formato de texto, mas **Base64 não é criptografia**: quem obtiver o arquivo poderá decodificar nomes, datas e anotações. O arquivo não contém uma assinatura nem um mecanismo de integridade criptográfica.

O importador também aceita um formato legado que usa AES/CBC/PKCS5Padding com chave e IV constantes embutidos no código. Essa forma legada não oferece autenticação de integridade e seus segredos não devem ser tratados como confidenciais. Não se deve interpretar a compatibilidade legada como garantia de confidencialidade do backup atual.

O importador adiciona os registros importados aos existentes; ele não faz deduplicação nem substituição automática. Linhas malformadas podem ser ignoradas pelo decodificador. Antes de importar, verifique a origem do arquivo e mantenha uma cópia dos dados atuais. O código não apresenta atualmente uma confirmação de sucesso da exportação com verificação posterior de leitura do arquivo, nem uma política de retenção de backups.

## Backup automático do Android

O manifesto contém `android:allowBackup="true"`, referencia `@xml/backup_rules` e `@xml/data_extraction_rules`. No estado atual, esses XMLs não declaram exclusões concretas do banco ou das configurações; são essencialmente modelos com as diretivas de inclusão/exclusão comentadas. O projeto define `minSdk = 34`, então os aparelhos suportados usam as regras de extração do Android 12 ou superior. Nesse conjunto de regras, o bloco `cloud-backup` está vazio e o bloco `device-transfer` está ausente; a documentação do Android define que, quando não há regras para um modo, o conteúdo elegível do app continua incluído por padrão (com exceções como cache e diretórios explicitamente reservados para não fazer backup). O Auto Backup inclui normalmente bancos SQLite criados pelo `SQLiteOpenHelper`. Portanto, **não trate o banco, o hash do PIN ou as preferências como excluídos do backup do sistema**. A disponibilidade e o comportamento de backup/transferência ainda podem variar conforme dispositivo, sistema e transporte configurado.

## Arquivos relevantes

- Persistência: `app/src/main/java/com/vega/remindr/data/RemindrDatabase.kt`
- Acesso à persistência: `app/src/main/java/com/vega/remindr/data/BirthdayRepository.kt`
- PIN e preferências de segurança: `app/src/main/java/com/vega/remindr/security/SecurityStore.kt`
- Formato de backup: `app/src/main/java/com/vega/remindr/data/BackupCodec.kt`
- Regras de backup Android: `app/src/main/res/xml/backup_rules.xml` e `app/src/main/res/xml/data_extraction_rules.xml`
- Notificações: `app/src/main/java/com/vega/remindr/notifications/BirthdayReceiver.kt`