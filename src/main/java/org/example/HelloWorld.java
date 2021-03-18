package org.example;

import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;

import org.redisson.api.*;
import org.redisson.codec.MarshallingCodec;
import org.redisson.config.Config;
import org.redisson.transaction.TransactionException;

/**
 * A hello world class taken from here: http://www.eclipse.org/jetty/documentation/jetty-10/programming-guide/index.html#creating-servlet
 */
public class HelloWorld extends HttpServlet
{
  RedissonClientAcquirer clientAcquirer;

  public HelloWorld() {
    super();
    Config redissonConfig = new Config();

    redissonConfig
        .setCodec(new MarshallingCodec(getClass().getClassLoader()))
        .useSingleServer()
        .setConnectTimeout(10_000)
        .setTimeout(10_000)
        .setRetryAttempts(0)
        .setConnectionPoolSize(20)
        .setConnectionMinimumIdleSize(1)
        .setPassword("SECRET")
        .setAddress("redis://host.docker.internal:6379");

    clientAcquirer = new RedissonClientAcquirer(redissonConfig);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    // In the wild, we wouldn't wait like this, but it makes testing easier.
    while(clientAcquirer.client() == null) {}

    RTransaction transaction = clientAcquirer.client().createTransaction(TransactionOptions.defaults());

    RMapCache<String, ExampleModel> mapCache = transaction.getMapCache("primaryCache");
    ExampleModel exampleModel = new ExampleModel("A message from model");
    mapCache.fastPut("test", exampleModel);

    ExampleModel value = mapCache.get("test");

    try {
      transaction.commit();
    } catch(TransactionException e) {
      transaction.rollback();
    }

    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().println("<h1>Hello Servlet</h1>");
    response.getWriter().println("<h1>Hello from the cache: " + value.message + "</h1>");
  }
}