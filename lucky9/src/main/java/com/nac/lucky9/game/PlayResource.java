/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.game;

import com.nac.lucky9.Lucky9;
import com.nac.lucky9.game.Command.CommandDecoder;
import com.nac.lucky9.game.Command.CommandEncoder;
import org.primefaces.push.EventBus;
import org.primefaces.push.RemoteEndpoint;
import org.primefaces.push.annotation.OnClose;
import org.primefaces.push.annotation.OnMessage;
import org.primefaces.push.annotation.OnOpen;
import org.primefaces.push.annotation.PathParam;
import org.primefaces.push.annotation.PushEndpoint;
import org.primefaces.push.annotation.Singleton;

/**
 *
 * @author camomon
 */
@PushEndpoint("/game/{gameId}/{accountId}")
@Singleton
public class PlayResource {

  @PathParam("gameId")
  private String gameId;

  @PathParam("accountId")
  private String accountId;

  @OnOpen
  public void onOpen(RemoteEndpoint r, EventBus eventBus) {
    Lucky9.infoInternal("onOpen " + gameId + ":" + accountId);
    eventBus.publish("/game/" + gameId + "/" + accountId, new Command(Long.parseLong(accountId), "CONNECT", null));
  }

  @OnClose
  public void onClose(RemoteEndpoint r, EventBus eventBus) {
    Lucky9.infoInternal("onclose " + gameId + ":" + accountId);
  }

  @OnMessage(decoders = {CommandDecoder.class}, encoders = {CommandEncoder.class})
  public Command onMessage(Command message) {
    return message;
  }

}
