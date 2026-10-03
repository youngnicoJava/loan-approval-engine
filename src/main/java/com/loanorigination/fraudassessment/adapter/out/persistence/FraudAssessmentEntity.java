package com.loanorigination.fraudassessment.adapter.out.persistence;

import com.loanorigination.fraudassessment.domain.FraudAssessmentDecision;
import com.loanorigination.fraudassessment.domain.FraudAssessmentResult;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name="fraud_assessments")
public class FraudAssessmentEntity extends PanacheEntityBase {
  @Id public UUID id;
  @Column(name="assessment_request_id",nullable=false,unique=true) public UUID requestId;
  @Column(name="loan_application_id",nullable=false) public UUID loanApplicationId;
  @Enumerated(EnumType.STRING) @Column(nullable=false,length=10) public FraudAssessmentDecision decision;
  @Column(name="fraud_score",nullable=false) public int score;
  @Column(name="risk_level",nullable=false,length=10) public String riskLevel;
  @JdbcTypeCode(SqlTypes.JSON) @Column(name="reason_codes",nullable=false,columnDefinition="jsonb") public String reasonCodes;
  @Column(name="ruleset_id",nullable=false,length=80) public String rulesetId;
  @Column(name="ruleset_version",nullable=false,length=40) public String rulesetVersion;
  @Column(name="evaluated_at",nullable=false) public Instant evaluatedAt;
  @Column(name="correlation_id",nullable=false,length=128) public String correlationId;

  static FraudAssessmentEntity from(FraudAssessmentResult r) {
    var e=new FraudAssessmentEntity(); e.id=r.fraudAssessmentId(); e.requestId=r.assessmentRequestId();
    e.loanApplicationId=r.loanApplicationId(); e.decision=r.decision(); e.score=r.fraudScore();
    e.riskLevel=r.riskLevel(); e.reasonCodes=write(r.reasonCodes()); e.rulesetId=r.rulesetId();
    e.rulesetVersion=r.rulesetVersion(); e.evaluatedAt=r.evaluatedAt(); e.correlationId=r.correlationId(); return e;
  }
  FraudAssessmentResult toDomain() {
    return new FraudAssessmentResult(id,requestId,loanApplicationId,decision,score,riskLevel,read(reasonCodes),
        rulesetId,rulesetVersion,evaluatedAt,correlationId);
  }
  private static String write(List<String> values) {
    try { return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(values); }
    catch(Exception e) { throw new IllegalStateException(e); }
  }
  private static List<String> read(String json) {
    try { return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json,new com.fasterxml.jackson.core.type.TypeReference<>(){}); }
    catch(Exception e) { throw new IllegalStateException(e); }
  }
}
