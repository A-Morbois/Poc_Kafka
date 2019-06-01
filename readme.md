**POC pour découvrir Kafka sur windows** || **Doc V1**

L'objectif est de découvrir kafka sur un poste en local tournant sur windows.

  
# Partie 1 :  
Installation de serveur + test console + test transfert de fichier.

  
## Etape 1 : Installation de Java

- récupérer Java ([https://www.java.com/fr/download/](https://www.java.com/fr/download/))  
- L'installer  
- Rajouter la configuration dans les variables d'environnement  
>JAVA_HOME = C:\Program Files\Java\jr1.8.0_212  
>PATH = %JAVA_HOME%\bin;

## Etape 2 : Installation de Zookeeper  
*Pour des raisons de POC et de simplicité, les installations se font directement sur C:\\*

- récupérer Zookerper ([https://www.apache.org/dyn/closer.cgi/zookeeper/](https://www.apache.org/dyn/closer.cgi/zookeeper/))  
- Renommer le fichier zoo_sample.cfg en zoo.cfg  
- modifier la variable DATADIR dans zoo.cfg  
>DATADIR = C:\zookeeper-3.5.5\data  
  
- Rajouter la configuration de zookeeper dans les variables d'environnement  
>ZOOKEEPER_HOME = C:\zookeeper-3.5.5

- Puis dans un terminal lancer le serveur  
```  
C:\zookeeper-3.5.5\bin>zkserver  
```

## Etape 3 : Installation de Kafka  
*Comme pour zookeper Pour des raisons de POC et de simplicité, les installations se font directement sur C:\\*

- Télécharger Kafka ([https://kafka.apache.org/downloads](https://kafka.apache.org/downloads))

- Configurer le (ou les serveurs) avec le fichier server.properties  
par exemple l'emplacement des logs  
```  
log.dirs=C:\kafka_2.12-2.2.0\kafka-logs  
```  
- Lancer le serveur  
>bin\windows\kafka-server-start.bat config\server.properties

## Etape 4.1 Job en console

On crée un producer  
>C:\kafka_2.12-2.2.0\bin\windows>kafka-console-producer.bat --broker-list localhost:9092 --topic test2

  
On créé le consumer qui vient  
>C:\kafka_2.12-2.2.0\bin\windows>kafka-console-consumer.bat --bootstrap-server localhost:9092 –topic test2  
  
## Etape 4.2 Job Standalone avec fichier  
*Job solo non distribué*  
Nous avons un fichier text en input (in.txt) qu'on veut faire passer dans kafka et écrire le tout dans un fichier output (out.txt)

On va configurer les fichiers de configuration pour pointer vers nos fichiers

>connect-file-in.properties  
```  
name=local-file-source  
connector.class=FileStreamSource  
tasks.max=1  
file=in.txt  
topic=StandaloneJob  
```

  
>connect-file-in.properties  
```  
name=local-file-sink  
connector.class=FileStreamSink  
tasks.max=1  
file=out.txt  
topics=StandaloneJob  
```  
  
On créé le topic  
>bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 1 --topic StandaloneJob  
  
  
>bin\windows\connect-standalone config\connect-standalone.properties config\connect-file-in.properties config\connect-file-out.properties  
-> il créé le topic tout seul

  
##TroubleShooting

>ERROR Shutdown broker because all log dirs in C:\kafka_2.12-2.2.0\kafka_2.12-2.2.0kafka-logs have failed (kafka.log.LogManager)  
>ERROR Error while creating ephemeral at /brokers/ids/0, node already exists and owner '72058176686456832' does not match current session '72058176686456835' (kafka.zk.KafkaZkClient$CheckedEphemeral)

Reset les logs de Zookeeper et Kafka  
- log.dirs=C:\kafka_2.12-2.2.0\kafka-logs  
- dataDir=C:\zookeeper-3.5.5\data

  
# Partie 2  
Job Java qui vient récupérer le cours de l'EUR/USD et qui vient l'upload dans Kafka;  
Doc à venir.
