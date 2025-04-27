package ptit.dblab.app.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import ptit.dblab.shared.common.entity.BaseEntity;
import ptit.dblab.app.enumerate.Role;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_entity")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class User extends BaseEntity{
	private String username;
	private String password;
	
	private String firstName;
	private String lastName;
	@Column(columnDefinition = "TEXT")
	private String avatar;
	
	private String email;
	private String phone;
	private LocalDate birthDay;
	
	@Enumerated(EnumType.STRING)
	private Role role;

	private String userCode;

	private String userPrefix;

	@Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean isPremium = false;

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
	@JsonManagedReference
	private List<UserClassRoom> classRooms;
}
