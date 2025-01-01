<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('phone_number'); section>
    <#if section = "header">
     <link rel="stylesheet" type="text/css" href="<link rel="stylesheet" type="text/css" href="/theme-resources/css/basic-data-registration-form.css">

    <#elseif section = "form">
			
			<form action="${url.loginAction}" method="post">
				
                <div>
                    <h2 id="${properties.kcFormHeader!}">
						${msg("secureYourAccountText")}
					</h2> 
                </div>
	
                <div>
    
					<div>
                     <label class="${properties.kcInputLabel!}">Password</label>

						<input id="password" type="password" name="password" class="${properties.kcCustomInput!}"
									 placeholder="Password" required aria-invalid="<#if messagesPerField.existsError('password')>true</#if>"/>

              <#if messagesPerField.existsError('password')>

								<span id="input-error-password" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('password'))?no_esc}
								</span>
              </#if>
					</div>
	
                     

                    <div>
                     <label class="${properties.kcInputLabel!}">Repeat password</label> 
						<input id="repeat_password" type="password" name="repeat_password" class="${properties.kcCustomInput!}"
									 placeholder="Repeat password" required aria-invalid="<#if messagesPerField.existsError('repeat_password')>true</#if>"/>
              <#if messagesPerField.existsError('repeat_password')>

								<span id="input-error-repeat-password" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('repeat_password'))?no_esc}
								</span>
              </#if>
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
					<div class="form-number-dot"></div>
					<div class="form-number-dot-selected"></div> 
					
				</div>
				</div>

				
			</form>


			<script>

            document.addEventListener('DOMContentLoaded', function() {

                var passwordInputField = document.getElementById('password');
                var repeatPasswordInputField = document.getElementById('repeat_password');

				console.log('execeuted domcontentloaded'); 

                passwordInputField.addEventListener('input', function(e) {
					console.log('password field event listener'); 
                    var passwordInput = e.target.value;
                    var repeatPasswordInput = repeatPasswordInputField.value;
                    var errorMessageId = 'input-error-repeat-password';
                    var existingErrorSpan = document.getElementById(errorMessageId);

                    if (passwordInput !== repeatPasswordInput) {
                        if (!existingErrorSpan) {
                            var errorSpan = document.createElement('span');
                            errorSpan.id = errorMessageId;
                            errorSpan.className = '${properties.kcInputFieldErrorMessageClass!}';
                            errorSpan.setAttribute('aria-live', 'polite');
                            errorSpan.innerHTML = 'Password and repeat password do not match';
	
                            passwordInputField.parentNode.insertAdjacentElement('afterend', errorSpan);
                        }
                        console.log('password NOT equal');
                    } else {
                        if (existingErrorSpan) {
                            existingErrorSpan.remove();
                        }
                        console.log('password IS EQUAL');
                    }
                });

                repeatPasswordInputField.addEventListener('input', function(e) {
                    var passwordInput = passwordInputField.value;
                    var repeatPasswordInput = e.target.value;
                    var errorMessageId = 'input-error-repeat-password';
                    var existingErrorSpan = document.getElementById(errorMessageId);

                    if (passwordInput !== repeatPasswordInput) {
                        if (!existingErrorSpan) {
                            var errorSpan = document.createElement('span');
                            errorSpan.id = errorMessageId;
                            errorSpan.className = '${properties.kcInputFieldErrorMessageClass!}';
                            errorSpan.setAttribute('aria-live', 'polite');
                            errorSpan.innerHTML = 'Password and repeat password do not match';

                            repeatPasswordInputField.parentNode.insertAdjacentElement('afterend', errorSpan);
                        }
                        console.log('password NOT equal');
                    } else {
                        if (existingErrorSpan) {
                            existingErrorSpan.remove();
                        }
                        console.log('password IS EQUAL');
                    }
                });
            });
        </script>


    </#if>
</@layout.registrationLayout> 