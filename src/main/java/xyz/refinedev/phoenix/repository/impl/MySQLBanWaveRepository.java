package xyz.refinedev.phoenix.repository.impl;

import xyz.refinedev.phoenix.profile.punishment.banwave.BanWave;
import xyz.refinedev.phoenix.profile.punishment.banwave.IBanWave;
import xyz.refinedev.phoenix.repository.IBanWaveRepository;
import xyz.refinedev.phoenix.utils.mysql.Attribute;
import xyz.refinedev.phoenix.utils.mysql.JSONColumn;
import xyz.refinedev.phoenix.utils.mysql.Key;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.mysql.UUIDColumn;
import xyz.refinedev.phoenix.utils.repository.mysql.MySQLListRepository;

import java.util.UUID;

public class MySQLBanWaveRepository extends MySQLListRepository<UUID, IBanWave>
        implements IBanWaveRepository {

    public MySQLBanWaveRepository() {
        super(
                BanWave.class,
                new Table() {

                    @Override
                    public String getName() {
                        return "pxwaves";
                    }

                    @Override
                    public Attribute[] getAttributes() {
                        return new Attribute[] {
                            Key.primary(UUIDColumn.of("id")),
                            UUIDColumn.of("id"),
                            JSONColumn.of("data")
                        };
                    }
                },
                IBanWave::getId);
    }
}
