# p2p-chat-redes
Chat P2P Cliente/Servidor implementado na linguagem Java, o projeto conta com:

Aplicativo Servidor
> - Confirmação de credenciais de login via Banco de Dados MySQL;</br>
> - Encapsulamento de cada conexão com Clientes, tornando cada sessão e suas informações isolada das outras;</br>
> - Armazenamento e atualização automática dos Clientes conectados e seus endereços/porta;</br>
> - Gerenciamento e conexão dos Clientes entre sí via Threading, com reconhecimento de comandos para recebimento e envio de informações;</br>
> - Utilização do protocolo TCP/IP.</br>

Aplicativo Cliente
> - Possibilidade de escolha de Host/Porta do Aplicativo Servidor para Conexão;</br>
> - Lista de amigos manualmente atualizável, com atualização de status (Login/Logoff) automática pelo servidor;</br>
> - Isolamento de envio e recebimento de comandos para o Aplicativo Servidor via Threading;</br>
> - Seleção automática de portas e solicitação de conexão para outros Clientes;</br>
> - Funcionamento Isolado do Aplicativo Servidor (Possibilidade de rodar apenas o Servidor em uma máquina e apenas o Cliente em outra);</br>
> - Possibilidade de múltiplas conversas simultâneamente;</br>
> - Funcionalidade de Anti-Spam no chat;</br>
> - Funcionalidade de Host (Ao solicitar que outro Cliente se conecte);</br>
> - Funcionalidade de Cliente (Ao conectar-se à outro Cliente atuando como Host).</br>

# FUNCIONAMENTO GERAL
Primeiramente deve-se iniciar o Aplicativo Servidor, para que os Clientes possam conectar-se e serem gerenciados.

Ao iniciar um cliente e preencher as informações de conexão:
> - Username;</br>
> - Senha;</br>
> - Host (Endereço IP do Aplicativo Servidor);</br>
> - Porta (Do Aplicativo Servidor).</br>
Após o preenchimento, uma requisição de conexão será enviada ao Aplicativo Servidor, o qual fará a autenticação das credenciais do usuário e, caso aceito, conectará o mesmo e guardará suas informações de conexão, definindo seu status como Online para todos os seus Contatos.

O Aplicativo Servidor então, para isolar as conexões entre os clientes, criará um novo objeto ServerThread, que implementa o funcionamento de um objeto Thread comum além de algumas adaptações realizadas para o funcionamento adequado neste cenário, este objeto ServerThread será responsável por receber e responder todos os comandos enviados pelo cliente, intermediando o contato entre Aplicativo Cliente, Aplicativo Servidor e Banco de Dados, encapsulando o cliente para que não tenha acesso à tais áreas. Um ponteiro para o objeto ServerThread de cada cliente é guardado juntamente com suas informações ao ser conectado.

O Cliente então é disponibilizado com uma tela FXML representativa de uma lista de amigos, onde possui a opção de atualizar os status de seus contatos manualmente. Ao atualizar, serão carregadas Labels com os usernames e os status de todos os seus contetos. Além disso, a lista também exibe o username da conta atualmente conectada, assim como o seu endereço IP e a porta sendo utilizada para conexão com o ServerThread.

Dentro do Cliente diversos comandos podem ser enviados e recebidos, sendo os principais:

Comandos de Envio
> - /loginRequest - (Solicitação de Login com as credenciais "Username" e "Senha" do usuário)
> - /userIdRequest - (Solicitação de Informação do ID do usuário atualmente conectado)
> - /getFriends - (Solicitação da Lista de Amigos do usuário atualmente conectado)
> - /getOnlineFriends - (Solicitação dos Amigos do usuário atualmente conectado que estão atualmente conectados ao Aplicativo Servidor)
> - /delClient - (Solicitação de exclusão das informações salvas do Cliente atualmente conectado no Aplicativo Servidor, utilizado ao resetar Conexão)
> - /requestFriendConnection - (Solicitação ao Servidor para que conecte um contato a este Cliente atualmente conectado)

Comandos de Recebimento
> - /loginStatus - (Recebe a informação se o Login foi efetuado com sucesso)
> - /userIdReturn - (Recebe o ID do usuário atualmente conectado)
> - /loadFriends - (Carrega todos os contatos recebidos do usuário atualmente conectado)
> - /loadOnlineFriends - (Carrega apenas os contatos Online do usuário atualmente conectado)
> - /connectToUserServer - (Recebe as informações e conecta-se ao Cliente contato cujo início do chat foi solicitado)

# FUNCIONAMENTO DA CONEXÃO À OUTRO CLIENTE
Ao ser solicitada a conexão com um contato, uma aplicação sClient será iniciada no host remetente, iniciando um servidor privado apenas para o chat em alguma de suas portas disponíveis, estas informações de conexão, então, são enviadas ao ServerThread, o qual filtra a mensagem e separa:
> - Ip do Cliente Servidor (Remetente)
> - Porta para conexão do Cliente Servidor (Remetente)
> - Username do contato à conectar-se (Destinatário)

Após a obtenção destas informações, o ServerThread envia uma solicitação ao servidor geral, que busca em seus registros as informações do contato à conectar-se, enviando ao objeto ServerThread alocado para o destinatário uma solicitação de conexão, com as informações necessárias. Esta informação então é repassada do ServerThread para o Cliente destinatário, iniciando uma segunda conexão (além do Aplicativo Servidor) com o Cliente Servidor (remetente), iniciando assim a janela de chat.

# IDEIAS PARA APRIMORAMENTO DO CÓDIGO

> - Atualização automática da lista de amigos - (Melhor funcionamento e implementação do FXML, ou substituição por outra plataforma)
> - Autenticação de IP ao conectar-se em outro cliente - (Cliente Remetente receber informação do IP correto a conectar-se, Cliente Destinatário autenticar-se ao solicitar conexão)
> - Otimização do Código - (Limpeza geral, arrumação, reimplementação menos complexa de funcionalidades)
> - Sistema de Envio de Arquivos via FTP - (Imagens, audios, videos... para outros contatos)
