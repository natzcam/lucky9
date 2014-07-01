/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.service;

import com.nac.lucky9.entity.Account;
import com.nac.lucky9.game.Game;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

@Singleton
@Startup
public class GameList implements Serializable {

  private final EventBus eventBus = EventBusFactory.getDefault().eventBus();
  @EJB
  private AccountService accountService;

  private List<Game> games = new ArrayList<>();

  @PostConstruct
  public void init() {
    List<Account> bankers = accountService.findBankerAccounts();
    for (Account banker : bankers) {
      games.add(new Game(banker));
    }
  }

  public List<Game> getGames() {
    return games;
  }

  public Game getGame(Long id) {

    for (Game g : games) {
      if (g.getId().equals(id)) {
        return g;
      }
    }
    return null;
  }

  public Game getCurrentGame(Long accountId) {
    for (Game g : games) {
      if (g.findPlayer(accountId) != null) {
        return g;
      }
    }
    return null;
  }

}
