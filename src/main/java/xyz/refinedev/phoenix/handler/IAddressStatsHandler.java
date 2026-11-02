package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.alt.AddressClassification;
import xyz.refinedev.phoenix.alt.IAddressStats;
import xyz.refinedev.phoenix.repository.IAddressStatsRepository;

import java.util.Map;
import java.util.Set;

public interface IAddressStatsHandler {

    String FINGERPRINT_PREFIX = "fp:";

    String SKIN_PREFIX = "skin:";

    String LOCATION_PREFIX = "loc:";

    IAddressStatsRepository getRepository();

    void recordNewAccount(String text, String text2);

    Map<String, IAddressStats> fetch(Set<String> values);

    IAddressStats getOrEmpty(String text);

    void forceClassification(String text, AddressClassification addressClassification);
}
