package kr.mybrary.userservice.user.persistence;

import jakarta.persistence.*;
import kr.mybrary.userservice.global.BaseEntity;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Table(name = "user_reports")
@AllArgsConstructor
@SQLDelete(sql = "UPDATE user_reports SET deleted = true WHERE report_id = ?")
@Where(clause = "deleted = false")
public class UserReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id")
    private User reported;

    @Column(nullable = false)
    private String reportReason;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

}
