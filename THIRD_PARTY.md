# Existing components and attribution

Original coursework packages and Git history are retained. This review does not introduce a new blanket project license or reattribute existing source.

The checked-in Maven wrapper keeps its Apache notices and pinned distribution URL. During review, wrapper JAR bytes were compared with their Maven Central artifacts: [Takari 0.5.6](https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/) or [Apache 3.1.0](https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.1.0/), as applicable. The original wrappers do not enforce the newer checksum properties; this audit-time comparison is not presented as a runtime integrity gate. See the official [Maven Wrapper documentation](https://maven.apache.org/wrapper/).

Spring, Hibernate, H2, Lombok, JUnit and Mockito retain their upstream licenses/notices through the declared dependencies. No font, photo, icon pack or generated app screenshot is added. New tests use fictional fixtures and no client/employer implementation or configuration.
