/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.game;

import com.nac.lucky9.entity.Account;
import com.nac.lucky9.game.Card.Suit;
import com.nac.lucky9.game.Card.Type;
import com.nac.lucky9.service.AccountService;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

/**
 *
 * @author Administrator
 */
public class Game implements Serializable {

  private final EventBus eventBus = EventBusFactory.getDefault().eventBus();
  private final List<Account> players = new ArrayList<>();
  private final List<Account> standbyPlayers = new ArrayList<>();
  private State state = State.BETTING;
  private final Long id;
  private final String name;
  private Account banker;
  private Account current = null;
  private List<Card> deck = new ArrayList<>(52);
  private List<Card> distCards = new ArrayList<>();
  private Timer timer = new Timer();
  private AccountService accountService = new AccountService();

  public Game(Account banker) {
    this.banker = accountService.find(banker.getId());
    this.id = banker.getId();
    this.name = banker.getName();
  }

  public String getName() {
    return name;
  }

  public State getState() {
    return state;
  }

  public List<Card> getDeck() {
    return deck;
  }

  public Account getBanker() {
    return banker;
  }

  public Account getCurrent() {
    return current;
  }

  public List<Account> getStandbyPlayers() {
    return standbyPlayers;

  }

  private Account getNextPlayer(Account p) {
    Account next = banker;
    int index = players.indexOf(p) + 1;
    if (index < players.size()) {
      next = players.get(index);
    }
    return next;
  }

  public Long getId() {
    return id;
  }

  public List<Account> getPlayers() {
    return players;
  }

  public List<Account> getPlayersWithBanker() {
    List<Account> pwb = new ArrayList<>();
    pwb.add(banker);
    pwb.addAll(players);
    return pwb;
  }

  public Account join(Account player) {
    if (player == null || banker.equals(player)
            || players.contains(player)
            || standbyPlayers.contains(player)) {
      return null;
    }
    
    player = accountService.find(player.getId());
    
    if (state == State.BETTING) {
      players.add(player);
    } else {
      standbyPlayers.add(player);
    }
    return player;
  }

  public void leave(Long playerId) {
    Account leaver = findPlayer(playerId);
    if (leaver == null || state == State.PLAYING) {
      return;
    }
    players.remove(leaver);
    standbyPlayers.remove(leaver);
  }

  public void setBet(Long playerId, int bet) {
    Account better = findPlayer(playerId);
    if (better == null || state == State.PLAYING || bet < 1) {
      return;
    }

    better.setBet(bet);
    better.setHit(false);
    better.setWin(0);
    better.getCards().clear();

    banker.setHit(false);
    banker.setWin(0);
    banker.getCards().clear();

    if (standbyPlayers.contains(better)) {
      standbyPlayers.remove(better);
      players.add(better);
    }

    timer.cancel();
    timer = new Timer();
    timer.schedule(new TimerTask() {

      @Override
      public void run() {

        startTurns();
      }
    }, 10000);

    eventBus.publish("/game/" + id + "/*", new Command(better.getId(), "SET_BET", better.getName() + " has bet " + bet));
  }

  private void startTurns() {
    if (state == State.BETTING) {
      state = State.PLAYING;
      banker.getCards().clear();

      shuffle();

      banker.getCards().add(distCard(false));
      banker.getCards().add(distCard(true));

      List<Account> toberemoved = new ArrayList<>();
      for (Account player : players) {

        player.setHit(false);
        player.setWin(0);

        if (player.getBet() > 0) {
          player.getCards().add(distCard(false));
          player.getCards().add(distCard(false));
        } else {
          toberemoved.add(player);
        }
      }
      players.removeAll(toberemoved);
      standbyPlayers.addAll(toberemoved);

      Account p = players.get(0);
      turn(p);
    }
  }

  public void setHit(Long playerId, boolean hit) {
    Account hitter = findPlayer(playerId);
    if (hitter == null || state == State.BETTING || hitter != current) {
      return;
    }

    hitter.setHit(hit);

    if (hitter.isHit()) {
      timer.cancel();
      hitter.getCards().add(distCard(true));
      eventBus.publish("/game/" + id + "/*", new Command(current.getId(), "TURN_HIT", current.getName() + " has hit"));
      turn(getNextPlayer(hitter));
    } else {
      timer.cancel();
      eventBus.publish("/game/" + id + "/*", new Command(current.getId(), "TURN_STAND", current.getName() + " has decided to stand"));
      turn(getNextPlayer(hitter));
    }
  }

  private synchronized void turn(final Account c) {

    if (state == State.PLAYING) {

      current = c;

      if (c == banker) {
        eventBus.publish("/game/" + id + "/*", new Command(current.getId(), "START_TURN_DEALER", current.getName() + "'s turn"));
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {

          @Override
          public void run() {
            if (banker.getScore() < 5) {
              banker.getCards().add(distCard(true));
              eventBus.publish("/game/" + id + "/*", new Command(current.getId(), "TURN_HIT", "Dealer has hit"));
            } else {
              eventBus.publish("/game/" + id + "/*", new Command(current.getId(), "TURN_STAND", "Dealer has decided to stand"));
            }
            endGame();
          }
        }, 3000);
      } else {
        eventBus.publish("/game/" + id + "/*", new Command(current.getId(), "START_TURN", current.getName() + "'s turn"));
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {

          @Override
          public void run() {
            eventBus.publish("/game/" + id + "/*", new Command(current.getId(), "TURN_STAND", current.getName() + " has defaulted to stand"));
            turn(getNextPlayer(c));
          }
        }, 10000);
      }
    }
  }

  public boolean isCurrent(Account account) {
    return current == account;
  }

  public boolean isBanker(Account account) {
    return banker == account;
  }

  public void endGame() {
    if (state == State.PLAYING) {
      state = State.BETTING;

      timer.cancel();
      timer = new Timer();
      timer.schedule(new TimerTask() {

        @Override
        public void run() {
          current = null;

          for (Account player : players) {

            if (player.getScore() > banker.getScore()) {
              player.setWin(player.getBet());
              player.addCredit(player.getBet());
              banker.addWin(-player.getBet());
              banker.addCredit(-player.getBet());
            } else if (player.getScore() < banker.getScore()) {
              player.setWin(-player.getBet());
              player.addCredit(-player.getBet());
              banker.addWin(player.getBet());
              banker.addCredit(player.getBet());
            }

            player.setBet(0);
            player.setHit(false);
          }

          accountService.savePlayers(players, banker.getId());

          turnUpCards();

          eventBus.publish("/game/" + id + "/*", new Command(banker.getId(), "END_GAME", "Game end"));
        }
      }, 1000);

    }
  }

  private void turnUpCards() {
    for (Card card : distCards) {
      card.setFaceUp(true);
    }
    for (Card card : deck) {
      card.setFaceUp(true);
    }
  }

  private Card distCard(boolean faceUp) {
    Card card = deck.remove(0);
    card.setFaceUp(faceUp);
    distCards.add(card);
    return card;
  }

  private void shuffle() {
    deck = new ArrayList<>(52);
    for (Suit suit : Suit.values()) {
      for (Type type : Type.values()) {
        deck.add(new Card(suit, type));
      }
    }
    Collections.shuffle(deck, new SecureRandom());
  }

  public Account findPlayer(Long playerId) {
    for (Account a : players) {
      if (a.getId().equals(playerId)) {
        return a;
      }
    }
    for (Account a : standbyPlayers) {
      if (a.getId().equals(playerId)) {
        return a;
      }
    }
    return null;
  }

  public static enum State {

    BETTING,
    PLAYING;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 29 * hash + Objects.hashCode(this.id);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Game other = (Game) obj;

    return Objects.equals(this.id, other.id);
  }

}
