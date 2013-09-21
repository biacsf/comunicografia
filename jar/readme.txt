Para gerar recomendações de acordo com um diálogo ocorrendo em um chat é necessário:

- importar o jar no projeto
- criar a pasta aonde ficará salvo um xml com toda a discussão gerada
- Executar a chamada ao método que vai analisar as mensagens e retornar a mensagem no formato: <a title="texto recomendado">palavra recomendada</a> restante da mensagem
- Chamada ao método: GeraRecomendacoes.geraRecomendacao(mensagem, usuario, idDialogo,caminhoPastaParaSalvarXmls);
- O metodo retorna o texto com as recomendações que deve ser enviado para a tela no lugar da mensagem que o usuário enviou
