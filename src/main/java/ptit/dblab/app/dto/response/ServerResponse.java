package ptit.dblab.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ptit.dblab.app.enumerate.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class ServerResponse {
	@JsonProperty("status")
	private int status;
	@JsonProperty("description")
	private String description;
	
	public ServerResponse(int status, String description) {
		super();
		this.status = status;
		this.description = description;
	}

	public ServerResponse(ErrorCode errorCode) {
		this.status = errorCode.getCode();
		this.description = errorCode.getDescription();
	}
}