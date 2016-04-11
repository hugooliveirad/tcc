# Pre Projeto

Markdown enquanto não converto para LaTeX com abntex2.

### Introdução

> Descrever o ambiente onde se encontra o problema. (e histórico?)
> Descrever o problema a ser estudado e como o as coisas acontecem atualmente.
> Descrever as possíveis propostas de soluções para o problema apresentado.
> Descrever quais são as possíveis vantagens e/ou benefícios que as soluções propostas trarão

Smartphones e Redes 3G ajudaram a difundir softwares de criação colaborativa ao redor do mundo, incluindo lugares com pouca infraestrutura de comunicação. Softwares que rodam nestas condições precisam levar em conta problemas que são encarados como casos raros em lugares mais desenvolvidos. Perdas de conexão de longa duração, baixo poder de processamento dos dispositivos e o alto custo da transferência de dados exigem uma arquitetura resiliente e que seja capaz de sincronizar versões de documentos com eficiência.

Analisando a arquitetura de software utilizada por ferramentas de edição colaborativa de documentos tradicionais, vemos uma grande dependência em um servidor que centraliza as edições e a comunicação entre diferentes clientes. Esta arquitetura centralizada é incompatível com redes que possuem grande latência e baixa estabilidade de conexão, já que em grande parte do tempo da edição o usuário estará esperando por uma resposta.

Uma arquitetura diferente, capaz de melhorar a experiência dos usuários em condições desfavoráveis, é a peer-to-peer (P2P). Nela os usuários trocam mensagens entre si, evitando a sincronização demorada com um servidor central. Após conectados, os usuários também são capazes de continuar colaborando em rede local, sem necessidade de enfrentar a latência de comunicação com um servidor longe.

Para sincronizar versões diferente de um documento editado concorrentemente por diversos usuários, precisamos de uma estrutura de dados capaz de ser fundida a outras versões sem conflitos. Isto é possível utiizando uma estrutura de dados CRDT (Conflict-free Replicated Data Type). Neste trabalho utilizaremos a estrutura de dados Logoot, proposta por Stéphane Weiss, Pascal Urso e Pascal Molli para implementar um software de edição colaborativa de documentos.

