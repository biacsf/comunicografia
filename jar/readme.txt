Para gerar recomenda��es de acordo com um di�logo ocorrendo em um chat � necess�rio:

- importar o jar no projeto
- criar a pasta aonde ficar� salvo um xml com toda a discuss�o gerada
- Executar a chamada ao m�todo que vai analisar as mensagens e retornar a mensagem no formato: <a title="texto recomendado">palavra recomendada</a> restante da mensagem
- Chamada ao m�todo: GeraRecomendacoes.geraRecomendacao(mensagem, usuario, idDialogo,caminhoPastaParaSalvarXmls);
- O metodo retorna o texto com as recomenda��es que deve ser enviado para a tela no lugar da mensagem que o usu�rio enviou
