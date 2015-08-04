# Introdução pré-projeto

Usuários distantes e conectados através da internet podem trabalhar juntos em documentos utilizando Sistemas de Edição Colaborativa. Estes sistemas permitem que usuários realizem operações em paralelo em um arquivo e que cada um veja as modificações dos outros em tempo real. Com essa funcionalidade, pessoas distantes fisicamente podem colaborar com pontos de vista, escrever documentos de texto de forma mais rápida e inclusive trabalhar em *pair-programming*.

Grande parte dos Edição Colaborativa dependem de servidores centralizadores, acarretando em uma infraestrutura cara e proibitiva para projetos/empresas simples. Este Trabalho de Conclusão de Curso implementará um sistema de Edição Colaborativa proposto por Stéphane Weiss, Pascal Urso e Pascal Molli entitulado Logoot. O Logoot foi escolhido pois é altamente escalável em projetos com muitas edições e permite a utilização de um esquema de comunicação descentralizado.

Sem a utilização de *tombstones* (linhas em branco que tomam o lugar de antigas linhas que foram editadas/removidas no arquivo), artefato de implementação de muitos outros sistemas de Edição Colaborativa, o Logoot adiciona pouco peso ao arquivo final mesmo após muitas edições feitas por muitos usuários. Isso acontece pois sistemas que dependem de *tombstones* adicionam peso ao arquivo de forma diretamente proporcional à quantidade de edições realizadas e o Logoot adiciona peso ao arquivo apenas de acordo com o tamanho do próprio conteúdo.

Utilizando uma comunicação peer-to-peer (P2P)ncia a falhas, resistência à censura e também colaboração *adhoc* e suporte a edição offline (Weiss, Urso, Molli, 2008).

Faz parte deste trabalho, implementar em JavaScript um módulo capaz de adicionar, remover e editar linhas de um arquivo de texto, um outro módulo para possibilitar a Edição Colaborativa através de um editor simples de texto que se comunique por P2P utilizando a tecnologia WebRealTImeComunication (WebRTC) e a infraestrutura necessária para que a Edição Colaborativa entre usuários distantes conectados à internet.

## Objetivo

- Implementar o sistema de edição colaborativa Logoot em um editor web com comunicação por P2P utilizando WebRTC

## Objetivos específicos

- Desenvolver módulo JavaScript que implemente o sistema Logoot e permita todas as suas funcionalidades: adição, remoção e edição de conteúdo.
- Implementar módulo JavaScript que transforme a estrutura de dados Logoot em editor de texto capaz de realizar operações de adição, remoção e edição de conteúdo no sistema
- Implementar estrutura de rede pra suportar a Edição Colaborativa por P2P utilizando a tecnologia WebRTC

## Referências bibliográficas

- https://hal.inria.fr/inria-00336191/PDF/main.pdf
- http://www.html5rocks.com/en/tutorials/webrtc/basics/
- http://futurice.com/blog/reactive-mvc-and-the-virtual-dom
- MDN, JavaScript
- Mais links sobre WebRTC
- Link sobre P2P
- Livro sobre programação funcional com JavaScript
- Links para pattern com Observables
- Links para o Cyclejs

#

- Contextualizar
- Dar o problema (portanto, servidor, demanda custo)
- Trabalho propõem problema de desenvolvimento
- Objetivo específico, melhorar o que eu vou estudar
- Objetivo geral é desenvolver uma aplicação, sistema) podemos eliminar custos da infraestrutura necessária para tal sistema funcionar, já que ele não mais dependerá de um servidor centralizador. Além disso, sistemas de Edição Colaborativa que funcionam através de P2P ganham funcionalidades como: toler
