package org.leiers.cloudflare;

import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

@Data
public class DnsRecord {
    private String id;
    private String zoneId;
    private String zoneName;
    private String name;
    private String type;
    private String content;
    private boolean proxiable;
    private boolean proxied;
    private int ttl;
    private boolean locked;
    private String createdOn;
    private String modifiedOn;

    @Override
    public String toString() {
        return "DnsRecord{" +
                "id='" + id + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", zoneName='" + zoneName + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", proxiable=" + proxiable +
                ", proxied=" + proxied +
                ", ttl=" + ttl +
                ", locked=" + locked +
                ", createdOn='" + createdOn + '\'' +
                ", modifiedOn='" + modifiedOn + '\'' +
                '}';
    }

    public static List<DnsRecord> parseFromApiResponse(String apiResponse) {
        List<DnsRecord> dnsRecords = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(apiResponse);
            JSONArray resultArray = (JSONArray) jsonResponse.get("result");

            for (Object obj : resultArray) {
                JSONObject jsonRecord = (JSONObject) obj;
                DnsRecord record = new DnsRecord();
                record.setId((String) jsonRecord.get("id"));
                record.setZoneId((String) jsonRecord.get("zone_id"));
                record.setZoneName((String) jsonRecord.get("zone_name"));
                record.setName((String) jsonRecord.get("name"));
                record.setType((String) jsonRecord.get("type"));
                record.setContent((String) jsonRecord.get("content"));
                record.setProxiable((Boolean) jsonRecord.get("proxiable"));
                record.setProxied((Boolean) jsonRecord.get("proxied"));
                record.setTtl(((Long) jsonRecord.get("ttl")).intValue());
                record.setLocked((Boolean) jsonRecord.get("locked"));
                record.setCreatedOn((String) jsonRecord.get("created_on"));
                record.setModifiedOn((String) jsonRecord.get("modified_on"));
                dnsRecords.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dnsRecords;
    }
}
