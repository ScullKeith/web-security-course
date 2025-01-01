<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=false displayMessage=false; section>
    
    <#if section = "header">
     <div></div>
    <#elseif section = "form">
		
  <#if messagesPerField.existsError('mobile_number')>
	
								<span id="input-error-mobile-code" class="${properties.kcCustomError!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('mobile_number'))?no_esc}
								</span>
              </#if>

			<form action="${url.loginAction}" method="post">
                  <div>
				  	<h2 id="${properties.kcFormHeader!}">${msg("verifyCodeTitle",(mobile_number!''))}</h2>
                </div>

                <div>
	
					<div>
                     <label class="${properties.kcInputLabel!}">Mobile code</label>

						<input type="tel" id="mobile_code" name="mobile_code" class="${properties.kcCustomInput!}"
									 placeholder="Mobile code" required aria-invalid="<#if messagesPerField.existsError('mobile_code')>true</#if>"/>
            
            
					</div>

				</div>
    
					<div class="submit-button-wrapper">
                        <button class="submit-button" type="submit" name="action" value="verify">
							<span class="button-text-continue">Verify</span> 
						</button>
					</div>

				</div>

		
			
			</form>
    
		<!-- Separate form for resending the code -->
            <form id="kc-restart-verification-form" class="kc-restart-verification-form" action="${url.loginAction}" method="post">
                <input type="hidden" name="action" value="resend">
                <a href="#" class="restart-verification-link" onclick="document.getElementById('kc-restart-verification-form').submit(); return false;">
                    Send new code
                </a>
            </form>
		
    </#if>
</@layout.registrationLayout> 