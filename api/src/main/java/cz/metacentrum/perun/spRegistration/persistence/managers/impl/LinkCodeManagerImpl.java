package cz.metacentrum.perun.spRegistration.persistence.managers.impl;

import cz.metacentrum.perun.spRegistration.Utils;
import cz.metacentrum.perun.spRegistration.persistence.managers.LinkCodeManager;
import cz.metacentrum.perun.spRegistration.service.exceptions.InternalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class LinkCodeManagerImpl implements LinkCodeManager {

    private static final Logger log = LoggerFactory.getLogger(LinkCodeManagerImpl.class);

    private static final String CODES_TABLE = "signatureCodes";

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public LinkCodeManagerImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public boolean validateCode(String code) {
        log.trace("validateCode({})", code);

        if (Utils.checkParamsInvalid(code)) {
            log.error("Wrong parameters passed: (code: {})", code);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        String query = new StringJoiner(" ")
                .add("SELECT count(code) FROM").add(CODES_TABLE)
                .add("WHERE code = :code")
                .toString();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("code", code);

        Integer foundCodes = jdbcTemplate.queryForObject(query, params, Integer.class);
        boolean isValid = ((foundCodes != null) && (foundCodes == 1));

        log.trace("validateCode() returns: {}", isValid);
        return isValid;
    }

    @Override
    @Transactional
    public int storeCodes(List<String> codes) throws InternalErrorException {
        log.trace("storeCodes({})", codes);

        if (Utils.checkParamsInvalid(codes) || codes.isEmpty()) {
            log.error("Wrong arguments passed: (codes: {})", codes);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        String query = new StringJoiner(" ")
                .add("INSERT INTO").add(CODES_TABLE).add("(code)")
                .add("VALUES (:code)")
                .toString();

        List<MapSqlParameterSource> batchArgs = new ArrayList<>();
        for (String code: codes) {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("code", code);
            batchArgs.add(parameters);
        }

        int[] insertedCodes = jdbcTemplate.batchUpdate(query, batchArgs.toArray(new MapSqlParameterSource[codes.size()]));
        int sum = 0;
        for (int i : insertedCodes) {
            if (i != 1) {
                log.error("Inserting code failed");
                throw new InternalErrorException("Inserting code failed");
            } else {
                sum++;
            }
        }

        if (sum != codes.size()) {
            log.error("Expected {} inserts, made {}", codes.size(), sum);
            throw new InternalErrorException("Expected " + codes.size() + " inserts, made " + sum);
        }

        log.trace("storeCodes() returns: {}", sum);
        return sum;
    }

    @Override
    public boolean deleteUsedCode(String code) throws InternalErrorException {
        log.trace("deleteUsedCode({})", code);

        if (Utils.checkParamsInvalid(code)) {
            log.error("Wrong parameters passed: (code: {})", code);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        String query = new StringJoiner(" ")
                .add("DELETE FROM").add(CODES_TABLE)
                .add("WHERE code = :code")
                .toString();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("code", code);

        int updatedCount = jdbcTemplate.update(query, params);

        if (updatedCount == 0) {
            log.error("Zero codes deleted, should delete at least one");
            throw new InternalErrorException(Utils.GENERIC_ERROR_MSG);
        } else if (updatedCount > 1) {
            log.error("Only one code should be deleted, more deleted: {}", updatedCount);
            throw new InternalErrorException(Utils.GENERIC_ERROR_MSG);
        } else {
            log.trace("deleteUsedCode() returns - codes deleted (void method)");
            return true;
        }
    }
}
