package arbuzica.exchange.blockchain;

public interface IBlockChain {

    String USER = "exchangerservice";
    String PASSWORD = "USHOrIMpUlInUTOm";

    double getWalletBalance();

    String endpoint();

}
