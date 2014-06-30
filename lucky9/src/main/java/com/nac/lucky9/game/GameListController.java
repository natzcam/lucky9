/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.game;

import com.nac.lucky9.service.GameList;
import com.nac.lucky9.Lucky9;
import com.nac.lucky9.controller.AccountController;
import com.nac.lucky9.entity.Account;
import com.nac.lucky9.service.AccountService;
import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author camomon
 */
@Named
@RequestScoped
public class GameListController {

  @Inject
  private AccountController accountController;
  @EJB
  private GameList gameList;
  @Inject
  private PlayController playController;
  @EJB
  private AccountService accountService;
  private Account sessionAccount;

  public Account getSessionAccount() {
    if (sessionAccount == null) {
      sessionAccount = accountService.find(accountController.getSessionAccountId());
    }
    return sessionAccount;
  }

  public void join(SelectEvent event) {
    Game game = (Game) event.getObject();
    playController.setGame(game);
    playController.setAccount(getSessionAccount());
    try {
      Faces.redirect("game/game.xhtml");
    } catch (IOException ex) {
      Lucky9.error(ex);
    }
  }

  public List<Game> getGames() {
    return gameList.getGames();
  }

  public Game getGame(Long id) {
    return gameList.getGame(id);
  }
}
