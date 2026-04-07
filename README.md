### Projeto de Conexão em Rede
Projeto de uma paródia do Club Penguim, para treinar conexões em Rede, usando arquitetura Cliente-Servidor.

### Como Compilar
  Para compilar o código manualmente via terminal basta viajar até a pasta onde
o diretório src está localizado, junto dele deve haver uma pasta bin (para onde irão as
classes compiladas) e uma pasta dist (onde estão os executáveis .jar) a partir dessa
pasta, que representa a pasta principal do projeto, o comando

`javac -sourcepath src/src/cliente/*.java src/compartilhado/*.java src/compartilhado/mensagens/*.java
src/server/*.java -d bin/`

deve ser executado, ele irá compilar todas as classes necessárias para o funcionamento do código. Essa forma de compilação via terminal
foi testada apenas no Windows 10, por isso não garantimos que irá funcionar em outro SO, mas basta compilar as classes do diretório principal que deve funcionar.
  
  Além disso, como nossa aplicação possuí uma GUI, alguns sprites foram
criados, eles se localizam na pasta src/sprites/ porém, a compilação via terminal não
conseguirá visualizar os sprites caso eles não estejam na pasta bin, por isso após a
compilação das classes você poderá executar o comando
`Copy-Item -Path "src\sprites" -Destination "bin" -Recurse` 
no terminal do Windows, ou o comando
`cp -r src/sprites bin/` 
em Linux/macOS. 
Ao final da execução destes comandos a nossa aplicação deve poder ser executada.

### Como Executar
  Para executar o código compilado via terminal a partir da pasta principal, caso
você esteja do lado do servidor deverá executar o código `java -cp bin/ server.Servidor`
e então iniciar o servidor normalmente. Caso esteja do lado do cliente você deverá
executar o código `java -cp bin/ cliente.Cliente` e então iniciar a conexão com o
servidor normalmente.
  
  Caso prefira utilizar o executável basta viajar até a pasta dist/ e dela utilizar o
comando de terminal `java -jar ServidorLobbyVoltantes.jar` do lado do servidor, e
então iniciar o servidor normalmente ou então `java -jar ClienteLobbyVoltante.jar` do
lado do cliente e então iniciar a conexão com o servidor normalmente.
  
  Ambas as formas de execução foram testadas em Windows 10 e 11 com
diferentes versões do Java, caso utilize outro sistema operacional é recomendado que
opte pela compilação e execução via terminal que se difere pouco de um SO para
outro.

### Nomeação
**Quais recursos precisam ser nomeados?**
	Basicamente todos os recursos que precisam ser acessados precisam ser nomeados, isto é, o API Gateway (o canal de comunicação entre microserviços), as instâncias dos microserviços (Auth/Login, Mercado, Inventário e Recompensas, bem como suas instâncias), as cópias de bancos de dados (O atual líder e as réplicas), o servidor do Event Bus, os tópicos de mensagem no event bus (para qual microserviço será a mensagem), os usuários, as cartas e as transações.

**Qual esquema de nomeação?**
	Para o acesso aos microserviços a nomeação será de forma estruturada, isto é, ao enviar a mensagem para o Event Bus ele deverá saber qual microserviço vai ser acessado e qual função será executada naquele microserviço, além de qual instância daquele microserviço será usada. Portanto, teremos algo como: 
	**\[serviço\].\[funcao\].\[id\]**
	Já para as cartas e os usuários o esquema será plano, poderemos armazená-los todos da mesma form a e para acessá-los utilizar uma função de hash para encontrar o ID da carta ou usuário específico.

**Dado o esquema qual o mecanismo de resolução de nomes?**
	Para os serviços os nomes serão resolvidos pelo DNS interno do Docker, traduzindo o nome estruturado mostrado anteriormente para um endereço IP respectivo para o servidor do microserviço.
	O Event Bus também terá de resolver nomes, já que receberá mensagens estruturadas e também mensagens planas, ele terá que identificar essas mensagens e se comunicar com o serviço correto.

### Processos
**Faz sentido usar threads?**
	Sim, afinal, os microserviços terão que se conectar ao Event Bus, para que essa conexão possa ser feita ele terá de estar aguardando sockets em segundo plano, o mesmo acontece para os microserviços, que estarão aguardando as respostas do Event Bus o tempo todo que estiverem conectados e isso terá de ser feito em threads.

**Servidores Stateful ou Stateless?**
	Stateless, os servidores dos microserviços não guardam nenhum estado, se eles caem eles se comunicam ao Banco de Dados onde as informações estão guardadas e atualizam para o estado atual. Ou seja, todas as informações e estados (Quantas cartas de cada estão em circulação, inventário dos jogadores, usuários inscritos, etc.) estarão armazenadas no banco de dados daquele microserviço.

**Faz sentido usar técnicas de virtualização?**
	Sim, iremos utilizar docker para ser possível executar todos os banco de dados e instâncias de microserviços, já que teremos alta replicação de cada um destes, o que resultaria em aproximadamente 20 instâncias executando em paralelo.
