/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.examples.server;

import io.atomix.AtomixServer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.NettyTransport;
import io.atomix.copycat.server.storage.Storage;

import java.net.InetAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Server example.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class StandaloneServerExample {

  /**
   * Starts the server.
   */
  public static void main(String[] args) throws Exception {
    if (args.length < 3)
      throw new IllegalArgumentException("must supply a path, a local port, and set of remote host:port tuples");

    // Parse the address to which to bind the server.
    int port = Integer.valueOf(args[1]);
    Address localAddress = new Address(InetAddress.getLocalHost().getHostName(), port);

    // Build a list of all member addresses to which to connect.
    List<Address> members = new ArrayList<>();
    members.add(localAddress);
    for (int i = 2; i < args.length; i++) {
      String[] parts = args[i].split(":");
      members.add(new Address(parts[0], Integer.valueOf(parts[1])));
    }

    AtomixServer server = AtomixServer.builder(localAddress, members)
        .withTransport(new NettyTransport())
        .withStorage(Storage.builder()
            .withDirectory(args[0])
            .withMinorCompactionInterval(Duration.ofSeconds(30))
            .withMajorCompactionInterval(Duration.ofMinutes(1))
            .withMaxSegmentSize(1024 * 1024 * 8)
            .withMaxEntriesPerSegment(1024 * 1024)
            .build())
        .build();

    server.open().join();

    while (server.isOpen()) {
      Thread.sleep(1000);
    }
  }

}