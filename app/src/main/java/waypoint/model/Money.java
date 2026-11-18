package waypoint.model;

import java.util.*;

/** VALUE OBJECT. Exact money. TODO: make every test in MoneyTest pass. */
public final class Money implements Comparable<Money> {
    private final long paise;
    private final Currency currency;

    private Money(long paise, Currency currency) {
        this.paise = paise;
        this.currency = Objects.requireNonNull(currency, "Currency must not be null");
    }

    public static Money ofMinor(long paise, Currency currency) {
        return new Money(paise, currency);
    }

    public static Money rupees(long rupees) {
        return new Money(Math.multiplyExact(rupees, 100L), Currency.getInstance("INR"));
    }

    public long paise() {
        return paise;
    }

    public Currency currency() {
        return currency;
    }

    public Money plus(Money other) {
        requireSameCurrency(other);
        return new Money(Math.addExact(this.paise, other.paise), this.currency);
    }

    public Money minus(Money other) {
        requireSameCurrency(other);
        return new Money(Math.subtractExact(this.paise, other.paise), this.currency);
    }

    public Money times(int factor) {
        return new Money(Math.multiplyExact(this.paise, factor), this.currency);
    }

    public boolean isNegative() {
        return paise < 0;
    }

    public List<Money> allocate(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Allocation count must be positive: " + n);
        }
        long baseShare = this.paise / n;
        long remainder = this.paise % n;
        int step = remainder >= 0 ? 1 : -1;
        long absRemainder = Math.abs(remainder);

        List<Money> shares = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            long share = baseShare + (i < absRemainder ? step : 0);
            shares.add(new Money(share, this.currency));
        }
        return Collections.unmodifiableList(shares);
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "Other money must not be null");
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatch(
                "Currency mismatch: " + this.currency + " vs " + other.currency
            );
        }
    }

    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return Long.compare(this.paise, other.paise);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money other)) return false;
        return this.paise == other.paise && this.currency.equals(other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paise, currency);
    }

    @Override
    public String toString() {
        long absPaise = Math.abs(paise);
        long major = absPaise / 100;
        long minor = absPaise % 100;
        String sign = paise < 0 ? "-" : "";
        return String.format("%s %s%d.%02d", currency.getCurrencyCode(), sign, major, minor);
    }
}

class CurrencyMismatch extends RuntimeException {
    public CurrencyMismatch(String message) {
        super(message);
    }
}