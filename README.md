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

