# Editor Colaborativo Descentralizado

## Introdução pré-projeto

Usuários distantes e conectados através da internet podem trabalhar juntos em documentos utilizando Sistemas de Edição Colaborativa. Estes sistemas permitem que usuários realizem operações em paralelo em um arquivo e que cada um veja as modificações dos outros em tempo real. Com essa funcionalidade, pessoas distantes fisicamente podem colaborar com pontos de vista, escrever documentos de texto de forma mais rápida e até trabalhar em *pair-programming*.

Grande parte dos Sistemas de Edição Colaborativa depende de servidores centralizadores, acarretando em uma infraestrutura cara e proibitiva para projetos/empresas de pequeno porte. Este Trabalho de Conclusão de Curso implementará um sistema de Edição Colaborativa proposto por Stéphane Weiss, Pascal Urso e Pascal Molli entitulado Logoot. O Logoot foi escolhido pois é altamente escalável em projetos com muitas edições e permite a utilização de um esquema de comunicação descentralizado.

Sem a utilização de *tombstones* (linhas em branco), artefato de implementação de muitos Sistemas de Edição Colaborativa, o Logoot adiciona pouco peso ao arquivo final mesmo após muitas edições feitas por diversos usuários. Isso acontece pois sistemas que dependem de *tombstones* adicionam um peso extra ao arquivo diretamente proporcional à quantidade de edições realizadas enquanto o Logoot adiciona peso ao arquivo apenas de acordo com o tamanho do próprio conteúdo.

Utilizando uma comunicação peer-to-peer (P2P), o sistema proposto para ser implementado neste trabalho oferece uma série de benefícios frente a outros sistema convencionais, como: tolerância a falhas, resistência à censura, colaboração *ad-hoc* e trabalho off-line (Weiss, Urso, Molli, 2008).

Faz parte deste trabalho, implementar em ClojureScript um módulo capaz de adicionar, remover e editar linhas de um arquivo de texto, um outro módulo para possibilitar a Edição Colaborativa através de um editor simples de texto que se comunique por P2P utilizando a tecnologia Web Real Time Communication (WebRTC) e a infraestrutura necessária para a Edição Colaborativa entre usuários distantes conectados à internet por P2P.

## Objetivo

- Implementar o sistema de edição colaborativa Logoot em um editor web com comunicação por P2P utilizando WebRTC

## Objetivos específicos

- Desenvolver módulo ClojureScript que implemente o sistema Logoot e permita todas as suas funcionalidades: adição, remoção e edição de conteúdo.
- Implementar módulo ClojureScript que transforme a estrutura de dados Logoot em um editor de texto capaz de realizar operações de adição, remoção e edição de conteúdo no sistema
- Implementar infraestrutura de rede pra suportar a Edição Colaborativa por P2P utilizando a tecnologia WebRTC

## Referências bibliográficas

- https://hal.inria.fr/inria-00336191/PDF/main.pdf
- http://www.html5rocks.com/en/tutorials/webrtc/basics/
- http://futurice.com/blog/reactive-mvc-and-the-virtual-dom
- MDN, JavaScript
- Mais links sobre WebRTC
- Link sobre P2P
- Livro sobre programação com Clojure
- Referências sobre ClojureScript
- Referências sobre React e Om
