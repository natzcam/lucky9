/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.game;

import com.nac.lucky9.Lucky9;
import com.nac.lucky9.service.GameList;
import com.nac.lucky9.entity.Account;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.validation.constraints.Max;
import org.omnifaces.util.Faces;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

/**
 *
 * @author camomon
 */
@Named
@SessionScoped
public class PlayController implements Serializable {

  private final EventBus eventBus = EventBusFactory.getDefault().eventBus();
  private Game game;
  private Long accountId;
  @Max(value = 1000)
  private int bet = 0;
  private int hit = -1;
  @EJB
  private GameList gameList;

  public int getBet() {
    return bet;
  }

  public void setBet(int bet) {
    this.bet = bet;
  }

  public void submitBet() {
    game.setBet(accountId, bet);
  }

  public void setHit(int hit) {
    this.hit = hit;
  }

  public int getHit() {
    return -1;
  }

  public String getEndGameMessage() {
    return game.getEndGameMessage(accountId);
  }

  public Account getAccount() {
    return game.findPlayer(accountId);
  }

  public void hitChange() {
    game.setHit(accountId, hit == 1);
    eventBus.publish("/game/" + game.getId() + "/*", new Command(accountId, "UPDATE_LIST", null));
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public List<Account> getPlayers() {
    return game.getPlayers();
  }

  public List<Account> getStandbyPlayers() {
    return game.getStandbyPlayers();
  }

  public List<Account> getPlayersWithBanker() {
    return game.getPlayersWithBanker();
  }

  public boolean isBanker(Account account) {
    return game.isBanker(account);
  }

  public boolean isCurrent(Account account) {
    return game.isCurrent(account);
  }

  public void connect() {
    Lucky9.infoInternal("connect " + game.getId());

    if (accountId == null) {
      Lucky9.infoInternal("null accountId");
      return;
    }

    if (game == null) {
      Lucky9.infoInternal("null game");
      return;
    }

    Game currentGame = gameList.getCurrentGame(accountId);
    if (currentGame != null) {
      currentGame.leave(accountId);
    }
    game.join(accountId);
  }

  public void disconnect() {

    if (game != null && accountId != null) {
      game.leave(accountId);
      eventBus.publish("/game/" + game.getId() + "/*", new Command(accountId, "UPDATE_LIST", null));
    }
    game = null;
    accountId = null;
    try {
      Faces.redirect("game/index.xhtml");
    } catch (IOException ex) {
      Lucky9.error(ex);
    }

  }
}
