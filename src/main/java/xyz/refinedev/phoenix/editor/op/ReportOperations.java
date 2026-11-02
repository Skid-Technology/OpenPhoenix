package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.editor.dto.ReportSummary;
import xyz.refinedev.phoenix.editor.panel.PanelPermissions;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IProof;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.report.ReportStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReportOperations {

    private final CommonPlatform platform;

    public ReportOperations(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            int value = (int) Math.min(EditorPayload.calculateTime(result2, "days", 14L), 90L);
            String text;
            ReportStatus reportStatus =
                    (text = EditorPayload.formatString(result2, "status", null)) != null
                                    && !"ALL".equalsIgnoreCase(text)
                            ? this.findReportStatus(text)
                            : null;
            List<IReport> values;
            try {
                values =
                        this.platform
                                .getReportHandler()
                                .getAllReports(value)
                                .get(10L, TimeUnit.SECONDS);
            } catch (Exception exception) {
                throw new EditorOperationException("INTERNAL", "the reports could not be read");
            }
            List<IReport> values2 = new ArrayList<>();
            Iterator iterator2 = values.iterator();
            while (iterator2.hasNext()) {
                IReport report;
                if ((report = (IReport) iterator2.next()) != null
                        && (reportStatus == null || report.getStatus() == reportStatus)) {
                    values2.add(report);
                }
            }
            values2.sort(Comparator.comparingLong(IReport::getIssuedAt).reversed());
            boolean matches;
            if (matches = values2.size() > 250) {
                values2 = values2.subList(0, 250);
            }
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).add("reports", this.create(values2));
            jsonObject.addProperty("truncated", matches);
            jsonObject.addProperty("days", value);
            return jsonObject;
        };
    }

    public EditorOpHandler getEditorOpHandlerByEditorOpHandler() {
        return (result, result2) -> {
            UUID uUID = EditorPayload.findUUID(result2, "id");
            ReportStatus reportStatus =
                    this.findReportStatus(EditorPayload.formatString(result2, "status"));
            Optional<IReport> optional;
            try {
                optional =
                        this.platform.getReportHandler().getReport(uUID).get(10L, TimeUnit.SECONDS);
            } catch (Exception exception) {
                throw new EditorOperationException("INTERNAL", "that report could not be read");
            }
            if (!optional.isPresent()) {
                throw new EditorOperationException("NO_SUCH_REPORT", "no report " + uUID);
            }
            IReport report;
            ReportStatus status = (report = (IReport) optional.get()).getStatus();
            report.setStatus(reportStatus);
            report.setResolvedBy(
                    reportStatus == ReportStatus.OPEN ? null : PanelPermissions.findUUID(result2));
            this.platform.getReportHandler().stamp(report, reportStatus);
            this.platform.getReportHandler().updateReport(report);
            if (reportStatus == ReportStatus.PUNISHED && status != ReportStatus.PUNISHED) {
                this.platform.getReportHandler().addPendingReward(report);
            }
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("id", uUID.toString());
            jsonObject.addProperty("status", reportStatus.name());
            jsonObject.addProperty(
                    "rewarded",
                    reportStatus == ReportStatus.PUNISHED && status != ReportStatus.PUNISHED);
            return jsonObject;
        };
    }

    private JsonArray create(List<IReport> values) {
        HashSet<UUID> values2 = new HashSet<>();
        Iterator<IReport> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            IReport report;
            if ((report = (IReport) iterator2.next()).getTarget() != null) {
                values2.add(report.getTarget());
            }
            if (report.getIssuedBy() != null) {
                values2.add(report.getIssuedBy());
            }
            if (report.getResolvedBy() != null) {
                values2.add(report.getResolvedBy());
            }
        }
        HashMap entries = new HashMap();
        if (!values2.isEmpty()) {
            Iterator iterator3 = this.platform.getProfileHandler().getProfiles(values2).iterator();
            while (iterator3.hasNext()) {
                IProfile profile;
                if ((profile = (IProfile) iterator3.next()) != null) {
                    entries.put(profile.getUniqueId(), profile.getName());
                }
            }
        }
        JsonArray jsonArray = new JsonArray();
        for (IReport report2 : values) {
            jsonArray.add(
                    this.platform
                            .getGson()
                            .toJsonTree(
                                    new ReportSummary(
                                            report2.getId(),
                                            report2.getCategoryId(),
                                            report2.getNiceReason(),
                                            report2.getStatus() == null
                                                    ? ReportStatus.OPEN.name()
                                                    : report2.getStatus().name(),
                                            report2.getTarget(),
                                            (String) entries.get(report2.getTarget()),
                                            report2.getTargetOn(),
                                            report2.getIssuedBy(),
                                            (String) entries.get(report2.getIssuedBy()),
                                            report2.getIssuedAt(),
                                            report2.getResolvedBy(),
                                            (String) entries.get(report2.getResolvedBy()),
                                            report2.getResolvedAt(),
                                            report2.wasPunished(),
                                            report2.getProof() == null
                                                    ? 0
                                                    : report2.getProof().size(),
                                            this.create(report2),
                                            report2.getCustomReason())));
        }
        return jsonArray;
    }

    private JsonArray create(IReport report) {
        JsonArray jsonArray = new JsonArray();
        if (report.getProof() == null) {
            return jsonArray;
        }
        Iterator<IProof> iterator2 = report.getProof().iterator();
        while (iterator2.hasNext()) {
            IProof proof;
            if ((proof = (IProof) iterator2.next()) != null) {
                JsonObject jsonObject;
                (jsonObject = new JsonObject())
                        .addProperty(
                                "type", proof.getType() == null ? null : proof.getType().name());
                jsonObject.addProperty("proof", proof.getProof());
                jsonObject.addProperty("addedBy", proof.getNiceAddedBy());
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    private ReportStatus findReportStatus(String text) {
        ReportStatus[] reportStatuss;
        int value = (reportStatuss = ReportStatus.values()).length;
        for (int index = 0; index < value; index++) {
            ReportStatus reportStatus;
            if ((reportStatus = reportStatuss[index]).name().equalsIgnoreCase(text)) {
                return reportStatus;
            }
        }
        throw new EditorOperationException("BAD_REQUEST", "unknown report status " + text);
    }
}
