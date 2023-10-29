import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Card {
    private final String family;
    private final String role;

    public Card(String family, String role) {
        this.family = family;
        this.role = role;
    }

    public String getFamily() {
        return family;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "Card{" +
                "family='" + family + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}

class Deck {
    private final List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        initializeDeck();
    }

    private void initializeDeck() {
        String[] families = {
                "Block, the Barber",
                "Bones, the Butcher",
                "Bun, the Baker",
                "Bung, the Brewer",
                "Chip, the Carpenter",
                "Dip, the Dyer",
                "Dose, the Doctor",
                "Grits, the Grocer",
                "Pots, the Painter",
                "Soot, the Sweep",
                "Tape, the Tailor"
        };

        for (String family : families) {
            cards.add(new Card(family, "father"));
            cards.add(new Card(family, "mother"));
            cards.add(new Card(family, "son"));
            cards.add(new Card(family, "daughter"));
        }
    }

    public void shuffle() {
        Collections.shuffle(cards, new Random());
    }

    public Card drawCard() {
        if (!cards.isEmpty()) {
            return cards.remove(cards.size() - 1);
        }
        return null;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}

class Player {
    private final String name;
    private final List<Card> hand;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void addCard(Card card) {
        hand.add(card);
    }
}

class HappyFamilyGame {
    private final Deck deck;
    private final List<Player> players;

    public HappyFamilyGame(int numPlayers) {
        deck = new Deck();
        deck.shuffle();
        players = new ArrayList<>();

        for (int i = 1; i <= numPlayers; i++) {
            players.add(new Player("Player " + i));
        }
    }

    public void dealCards(int numCardsPerPlayer) {
        for (int i = 0; i < numCardsPerPlayer; i++) {
            for (Player player : players) {
                Card card = deck.drawCard();
                if (card != null) {
                    player.addCard(card);
                }
            }
        }
    }

    public void play() {
        int currentPlayerIndex = 0;
        Player currentPlayer = players.get(currentPlayerIndex);
        boolean gameWon = false;

        while (!deck.isEmpty() && !gameWon) {
            System.out.println(currentPlayer.getName() + "'s turn.");
            displayPlayerHand(currentPlayer);

            String family = promptForFamily();
            String role = promptForRole();

            if (currentPlayerHasCard(currentPlayer, family, role)) {
                Player targetPlayer = selectTargetPlayer(currentPlayerIndex);
                Card requestedCard = getRequestedCard(currentPlayer, family, role);

                if (targetPlayerHasCard(targetPlayer, requestedCard)) {
                    // Give the card to the asking player
                    targetPlayer.getHand().remove(requestedCard);
                    currentPlayer.addCard(requestedCard);
                } else {
                    System.out.println(targetPlayer.getName() + " says: 'Pick a card!'");
                    Card drawnCard = deck.drawCard();
                    System.out.println("Drawn card: " + drawnCard);
                    if (drawnCard != null && drawnCard.getFamily().equals(family)) {
                        System.out.println("Lucky dip!");
                        currentPlayer.addCard(drawnCard);
                    }
                }
            } else {
                System.out.println("You don't have the requested card.");
            }

            if (currentPlayerHasFullFamily(currentPlayer, family)) {
                System.out.println(currentPlayer.getName() + " says: 'Happy family!'");
                removeFullFamilyFromHand(currentPlayer, family);
                if (currentPlayer.getHand().isEmpty()) {
                    gameWon = true;
                }
            }

            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            currentPlayer = players.get(currentPlayerIndex);
        }

        // Determine the winner
        Player winner = determineWinner();
        System.out.println("Game over! " + winner.getName() + " wins!");
    }

    private String promptForFamily() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the family name: ");
        String name = scanner.nextLine();
        scanner.close();
        return name;
    }

    private String promptForRole() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the role (father, mother, son, daughter): ");
        String role = scanner.nextLine();
        scanner.close();
        return role;
    }

    private boolean currentPlayerHasCard(Player player, String family, String role) {
        for (Card card : player.getHand()) {
            if (card.getFamily().equals(family) && card.getRole().equals(role)) {
                return true;
            }
        }
        return false;
    }

    private Player selectTargetPlayer(int currentPlayerIndex) {
        int targetPlayerIndex = (currentPlayerIndex + 1) % players.size();
        return players.get(targetPlayerIndex);
    }

    private Card getRequestedCard(Player player, String family, String role) {
        for (Card card : player.getHand()) {
            if (card.getFamily().equals(family) && card.getRole().equals(role)) {
                return card;
            }
        }
        return null;
    }

    private boolean targetPlayerHasCard(Player player, Card card) {
        return player.getHand().contains(card);
    }

    private void displayPlayerHand(Player player) {
        System.out.println(player.getName() + "'s hand:");
        for (Card card : player.getHand()) {
            System.out.println(card);
        }
    }

    private boolean currentPlayerHasFullFamily(Player player, String family) {
        int count = 0;
        for (Card card : player.getHand()) {
            if (card.getFamily().equals(family)) {
                count++;
            }
        }
        return count >= 4;
    }

    private void removeFullFamilyFromHand(Player player, String family) {
        List<Card> cardsToRemove = new ArrayList<>();
        for (Card card : player.getHand()) {
            if (card.getFamily().equals(family)) {
                cardsToRemove.add(card);
            }
        }
        player.getHand().removeAll(cardsToRemove);
    }

    private Player determineWinner() {
        Player winner = players.get(0);
        for (Player player : players) {
            if (player.getHand().size() > winner.getHand().size()) {
                winner = player;
            }
        }
        return winner;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of players (2-4): ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine(); // Consume the newline
        scanner.close();

        if (numPlayers < 2 || numPlayers > 4) {
            System.out.println("Invalid number of players. The game supports 2-4 players.");
            return;
        }

        HappyFamilyGame game = new HappyFamilyGame(numPlayers);
        game.dealCards(8 / numPlayers); // Deal 8 cards in total, evenly distributed among players
        game.play();

    }
}
