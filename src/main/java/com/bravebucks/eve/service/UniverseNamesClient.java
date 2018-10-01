package com.bravebucks.eve.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.bravebucks.eve.domain.esi.UniverseName;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UniverseNamesClient {

    private final RestTemplate restTemplate;

    public UniverseNamesClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Id/name association for an ID. All IDs must resolve to a name, or a 404 will be thrown.
     *
     * @param id
     * @return
     */
    public String get(Integer id) {
        return get(Collections.singletonList(id)).get(id);
    }

    /**
     * List of id/name associations for a set of IDs. All IDs must resolve to a name, or a 404 will be thrown.
     *
     * @param ids
     * @return
     */
    public Map<Integer, String> get(List<Integer> ids) {
        Map<Integer, String> result = new HashMap<>();

        // remove duplicates
        ids = ids.stream().distinct().collect(Collectors.toList());
        addNames(ids, result);

        return result;
    }

    void addNames(List<Integer> ids, Map<Integer, String> result) {
        for (int i = 0; i < ids.size(); i += 1000) {
            int chunkSize = getNextChunkSize(ids.size(), i, 1000);
            final List<Integer> chunk = ids.subList(i, i + chunkSize);
            final UniverseName[] body = doCall(chunk).getBody();
            Arrays.stream(Objects.requireNonNull(body))
                .forEach(el -> result.put(el.getId(), el.getName()));
        }
    }

    int getNextChunkSize(int size, int current, int maxChunkSize) {
        boolean isEnd = current + maxChunkSize > size;
        return isEnd ? size - current : maxChunkSize;
    }

    ResponseEntity<UniverseName[]> doCall(List<Integer> ids) {
        final String url = "https://esi.evetech.net/v2/universe/names/";
        final HttpEntity<Integer[]> request = new HttpEntity(ids, null);
        return restTemplate.exchange(url, HttpMethod.POST, request, UniverseName[].class);
    }
}
