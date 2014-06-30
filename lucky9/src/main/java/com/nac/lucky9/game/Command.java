/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.game;

import com.google.gson.Gson;
import org.primefaces.push.Decoder;
import org.primefaces.push.Encoder;

/**
 *
 * @author camomon
 */
public class Command {

  private Long originId;
  private String command;
  private String[] parameters;
  private String message;

  public Command(Long originId, String command, String[] parameters, String message) {
    this.originId = originId;
    this.command = command;
    this.parameters = parameters;
    this.message = message;
  }
  
  public Command(Long originId, String command, String message) {
    this.originId = originId;
    this.command = command;
    this.parameters = null;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public Long getOriginId() {
    return originId;
  }

  public void setOriginId(Long originId) {
    this.originId = originId;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String[] getParameters() {
    return parameters;
  }

  public void setParameters(String[] parameters) {
    this.parameters = parameters;
  }

  public static class CommandDecoder implements Decoder<String, Command> {

    private final Gson gson = new Gson();

    @Override
    public Command decode(String s) {
      return gson.fromJson(s, Command.class);
    }
  }

  public static class CommandEncoder implements Encoder<Command, String> {

    private final Gson gson = new Gson();

    @Override
    public String encode(Command u) {
      return gson.toJson(u);
    }

  }
}
