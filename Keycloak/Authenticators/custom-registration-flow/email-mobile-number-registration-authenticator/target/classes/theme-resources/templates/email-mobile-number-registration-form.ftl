<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=false displayMessage=false; section>

    <#if section = "header">
     <div></div>
    <#elseif section = "form">
				
			<form action="${url.loginAction}" method="post">
                  <div>
				  	<h2 id="${properties.kcFormHeader!}">${msg("greetingMessage",(first_name!''))}!</h2>
                </div>

                <div>
	
					<div>
                     <label class="${properties.kcInputLabel!}">Email</label>

						<input type="tel" id="email" name="email" class="${properties.kcCustomInput!}"
									 placeholder="Email" required aria-invalid="<#if messagesPerField.existsError('email')>true</#if>"/>

					

					</div>


					<#if messagesPerField.existsError('email')>
	
								<span id="input-error-email" class="${properties.kcInputFieldErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('email'))?no_esc}
								</span>
              </#if>

                    <div>

                     <label class="${properties.kcInputLabel!}">Mobile number</label> 

						<input type="tel" id="mobile_number" name="mobile_number" class="${properties.kcCustomInput!}"
									 placeholder="###-###-####" required aria-invalid="<#if messagesPerField.existsError('mobile_number')>true</#if>"/>


					<#if messagesPerField.existsError('mobile_number')>
	
								<span id="input-error-mobile-number" class="${properties.kcInputFieldErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('mobile_number'))?no_esc}
								</span>
              </#if>

<script>
    document.getElementById('mobile_number').addEventListener('input', function (e) {
        var input = e.target.value;
        input = input.replace(/\D/g, '');
        if (input.length > 3 && input.length <= 6) {
            input = input.slice(0, 3) + '-' + input.slice(3);
        } else if (input.length > 6) {
            input = input.slice(0, 3) + '-' + input.slice(3, 6) + '-' + input.slice(6, 10);
        }
        e.target.value = input;
    });
						</script>



							
					</div> 
				</div>

					<div class="submit-button-wrapper">
                        <button class="submit-button" type="submit">
							<span class="button-text-continue"> Continue </span> 
        					<span class="button-text-arrow">></span> 
						</button>
					</div> 
					
				 
				

						<div class="form-number-dots-wrapper">

					<div class="form-number-dot"></div>
					<div class="form-number-dot-selected"></div>
					<div class="form-number-dot"></div> 
					
				</div>
				</div>

				
			</form>
    </#if>
</@layout.registrationLayout> 