package kr.co.pincoin.api.domain.shop.model.store;

import kr.co.pincoin.api.infra.shop.entity.store.StoreEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Store {
    private final Long id;
    private final String name;
    private final String code;
    private final LocalDateTime created;
    private final LocalDateTime modified;
    private String theme;
    private String phone;
    private String phone1;
    private String kakao;
    private String bankAccount;
    private String escrowAccount;
    private Integer chunkSize;
    private Integer blockSize;
    private Boolean signupOpen;
    private Boolean underAttack;

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "instanceBuilder")
    private Store(String name, String code, String theme,
                  String phone, String phone1, String kakao,
                  String bankAccount, String escrowAccount,
                  Integer chunkSize, Integer blockSize) {
        this.id = null;
        this.name = name;
        this.code = code;
        this.theme = theme;
        this.phone = phone;
        this.phone1 = phone1;
        this.kakao = kakao;
        this.bankAccount = bankAccount;
        this.escrowAccount = escrowAccount;
        this.chunkSize = chunkSize != null ? chunkSize : 10;
        this.blockSize = blockSize != null ? blockSize : 10;
        this.signupOpen = true;
        this.underAttack = false;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();

        validateStore();
    }

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "jpaBuilder")
    private Store(Long id, String name, String code, String theme,
                  String phone, String phone1, String kakao,
                  String bankAccount, String escrowAccount,
                  Integer chunkSize, Integer blockSize,
                  Boolean signupOpen, Boolean underAttack,
                  LocalDateTime created, LocalDateTime modified) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.theme = theme;
        this.phone = phone;
        this.phone1 = phone1;
        this.kakao = kakao;
        this.bankAccount = bankAccount;
        this.escrowAccount = escrowAccount;
        this.chunkSize = chunkSize;
        this.blockSize = blockSize;
        this.signupOpen = signupOpen;
        this.underAttack = underAttack;
        this.created = created;
        this.modified = modified;

        validateStore();
    }

    public static Store of(String name, String code, String theme,
                           String phone, String phone1, String kakao,
                           String bankAccount, String escrowAccount,
                           Integer chunkSize, Integer blockSize) {
        return Store.instanceBuilder()
                .name(name)
                .code(code)
                .theme(theme)
                .phone(phone)
                .phone1(phone1)
                .kakao(kakao)
                .bankAccount(bankAccount)
                .escrowAccount(escrowAccount)
                .chunkSize(chunkSize)
                .blockSize(blockSize)
                .build();
    }

    public static Store from(StoreEntity entity) {
        return Store.jpaBuilder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .theme(entity.getTheme())
                .phone(entity.getPhone())
                .phone1(entity.getPhone1())
                .kakao(entity.getKakao())
                .bankAccount(entity.getBankAccount())
                .escrowAccount(entity.getEscrowAccount())
                .chunkSize(entity.getChunkSize())
                .blockSize(entity.getBlockSize())
                .signupOpen(entity.getSignupOpen())
                .underAttack(entity.getUnderAttack())
                .created(entity.getCreated())
                .modified(entity.getModified())
                .build();
    }

    public void updateTheme(String theme) {
        if (theme == null || theme.trim().isEmpty()) {
            throw new IllegalArgumentException("Theme cannot be empty");
        }
        this.theme = theme;
    }

    public void updateContactInfo(String phone, String phone1, String kakao) {
        this.phone = phone;
        this.phone1 = phone1;
        this.kakao = kakao;
    }

    public void updateBankAccounts(String bankAccount, String escrowAccount) {
        this.bankAccount = bankAccount;
        this.escrowAccount = escrowAccount;
    }

    public void updatePagination(Integer chunkSize, Integer blockSize) {
        if (chunkSize != null && chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be positive");
        }
        if (blockSize != null && blockSize <= 0) {
            throw new IllegalArgumentException("Block size must be positive");
        }

        this.chunkSize = chunkSize;
        this.blockSize = blockSize;
    }

    public void openSignup() {
        this.signupOpen = true;
    }

    public void closeSignup() {
        this.signupOpen = false;
    }

    public void markAsUnderAttack() {
        this.underAttack = true;
        this.closeSignup();
    }

    public void markAsSafe() {
        this.underAttack = false;
    }

    public boolean isAcceptingSignups() {
        return this.signupOpen && !this.underAttack;
    }

    public boolean hasValidContactInfo() {
        return (phone != null && !phone.trim().isEmpty()) ||
                (phone1 != null && !phone1.trim().isEmpty()) ||
                (kakao != null && !kakao.trim().isEmpty());
    }

    public boolean hasValidBankInfo() {
        return (bankAccount != null && !bankAccount.trim().isEmpty()) ||
                (escrowAccount != null && !escrowAccount.trim().isEmpty());
    }

    private void validateStore() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Store name cannot be empty");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Store code cannot be empty");
        }
        if (chunkSize != null && chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be positive");
        }
        if (blockSize != null && blockSize <= 0) {
            throw new IllegalArgumentException("Block size must be positive");
        }
    }
}
