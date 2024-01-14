package arbuzica.exchange.database.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import arbuzica.exchange.utilities.java.StringUtility;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document(collection = "serials")
public class Serial {

    @Id
    private String id;

    private String redeemerId;
    private String code;
    private Double credits;
    private Long issueDate;
    private Long usageDate;

    @Builder
    private Serial(Double credits) {
        this.redeemerId = "";
        this.code = StringUtility.randomCode(3);
        this.credits = credits;
        this.issueDate = System.currentTimeMillis();
        this.usageDate = 0L;
    }

    public boolean isRedeemed() {
        return !this.redeemerId.isEmpty();
    }
}
