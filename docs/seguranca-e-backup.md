# Remindr — segurança, PIN, proteção de dados e backups

## Dados armazenados

O Remindr mantém os aniversários em um banco SQLite local (`remindr.db`). Cada registro contém nome, data de nascimento e anotações. A tabela `config` armazena valores como o hash e o salt do PIN, a contagem de tentativas fracassadas, o fim de um bloqueio temporário, a preferência de biometria e o estado da introdução inicial. O código de acesso ao banco está em `app/src/main/java/com/vega/remindr/data/RemindrDatabase.kt`.

O banco é mantido no armazenamento privado da aplicação pelo mecanismo padrão do Android. Isso fornece o isolamento normal do sandbox do aplicativo, mas **não significa que o conteúdo do SQLite esteja criptografado por uma camada própria do Remindr**. O código usa `SQLiteOpenHelper` e não configura SQLCipher nem outra criptografia de banco de dados. O esquema está na versão 2; a migração da versão 1 para a 2 cria um índice não destrutivo e preserva os aniversários e configurações existentes. Um dispositivo comprometido, com root ou depuração indevida, pode contornar as proteções de interface. O backup automático do Android está desativado no manifesto e as regras de extração também excluem os dados locais do backup na nuvem e na transferência entre dispositivos.

## PIN e biometria

- O PIN tem quatro dígitos e o fluxo de criação pede confirmação duas vezes.
- O app guarda um hash SHA-256 do PIN combinado com um salt aleatório de 16 bytes, gerado separadamente para cada PIN. O salt e o hash ficam na tabela `config`; o PIN em texto puro não é armazenado. SHA-256 não é uma função deliberadamente lenta de derivação, então um PIN de quatro dígitos continua tendo um espaço de busca pequeno se os dados de configuração forem obtidos. Os hashes sem salt da versão anterior são reconhecidos e atualizados para o formato com salt assim que o PIN correto for informado.
- Após cinco tentativas incorretas consecutivas, a digitação do PIN fica bloqueada por 60 segundos. A contagem e o fim do bloqueio são persistidos, o pop-up informa o bloqueio e o teclado numérico permanece desabilitado até o prazo terminar. A biometria continua disponível durante esse período, quando estiver habilitada.
- O bloqueio é uma barreira de interface. Ele não criptografa o banco nem as anotações.
- A biometria usa `BiometricPrompt` com `BIOMETRIC_STRONG`; a verificação biométrica em si é feita pelo Android. O Remindr armazena somente a preferência de habilitação, não a impressão digital.
- A recuperação/redefinição do PIN depende de autenticação biométrica no fluxo atual. Se a biometria não estiver disponível e o PIN for esquecido, o código não oferece uma recuperação alternativa.

## Exportação e importação manual

A exportação gera um arquivo de texto (`text/plain`) com o cabeçalho `REMINDR_BACKUP_V2`. O conteúdo é criptografado com **AES-256-GCM**, incluindo verificação de integridade, usando uma chave derivada de uma senha definida pelo usuário por PBKDF2-HMAC-SHA-256, com salt aleatório de 16 bytes e 210.000 iterações. O nonce também é aleatório por arquivo. A senha não é salva no aplicativo e precisa ser guardada pelo usuário; ela é necessária para descriptografar o backup, inclusive ao restaurá-lo em outro aparelho. O arquivo não contém nomes, datas ou anotações em texto legível.

O importador continua aceitando os formatos anteriores para compatibilidade: `REMINDR_BACKUP_V1`, que usa Base64 e **não é criptografado**, e o formato legado que usa AES/CBC/PKCS5Padding com chave e IV constantes embutidos no código. O formato legado não oferece autenticação de integridade, e os segredos embutidos não devem ser tratados como confidenciais. Esses formatos antigos só são aceitos na importação; as novas exportações sempre usam o formato V2 criptografado.

Antes de gravar, o importador valida o cabeçalho/formato, a estrutura de cada linha, os campos, as datas e os limites de tamanho e quantidade de registros. Arquivos vazios, malformados, excessivamente grandes, com registros inválidos ou com senha incorreta são rejeitados. A importação de todos os registros é executada em uma única transação SQLite: se qualquer inserção falhar, a transação é revertida e nenhuma importação parcial fica gravada. Os registros importados são adicionados aos existentes; não há deduplicação automática. Mantenha a senha do backup em um local seguro e verifique a origem do arquivo antes de importá-lo.

## Backup automático do Android

O manifesto contém `android:allowBackup="false"` e continua referenciando `@xml/backup_rules` e `@xml/data_extraction_rules`. Os dois arquivos XML declaram exclusões explícitas para arquivos, bancos de dados, preferências e demais domínios de dados da aplicação, tanto para backup na nuvem quanto para transferência entre dispositivos. Assim, o banco SQLite, o hash e o salt do PIN e as demais configurações não devem ser incluídos pelo mecanismo de backup/transferência do Android. A disponibilidade e o comportamento exato ainda podem variar conforme dispositivo e fabricante; por isso, não trate o backup automático do sistema como um recurso de recuperação do Remindr. Use a exportação manual criptografada quando precisar preservar ou transferir os aniversários.

## Arquivos relevantes

- Persistência e migrações SQLite: `app/src/main/java/com/vega/remindr/data/RemindrDatabase.kt`
- Acesso à persistência e importação transacional: `app/src/main/java/com/vega/remindr/data/BirthdayRepository.kt`
- PIN, salt e bloqueio por tentativas: `app/src/main/java/com/vega/remindr/security/SecurityStore.kt`
- Formato e criptografia de backup: `app/src/main/java/com/vega/remindr/data/BackupCodec.kt`
- Regras de backup Android: `app/src/main/res/xml/backup_rules.xml` e `app/src/main/res/xml/data_extraction_rules.xml`
- Manifesto e eventos de reagendamento: `app/src/main/AndroidManifest.xml` e `app/src/main/java/com/vega/remindr/notifications/BootReceiver.kt`
- Cálculo, agendamento e entrega dos lembretes: `app/src/main/java/com/vega/remindr/notifications/ReminderDateCalculator.kt`, `app/src/main/java/com/vega/remindr/notifications/ReminderScheduler.kt` e `app/src/main/java/com/vega/remindr/notifications/BirthdayReceiver.kt`. O app reage à inicialização, atualização do pacote, alteração de data/hora/fuso e mudança da permissão de alarmes exatos. Ao voltar ao primeiro plano, ele recalcula os alarmes, usa alarmes inexatos quando não há permissão para exatidão e tenta entregar o lembrete de hoje caso a hora das 9h já tenha passado; o ano da notificação é registrado para evitar duplicatas.
