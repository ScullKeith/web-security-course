<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=false displayMessage=false; section>

    <#if section = "header">
     <div></div>
    <#elseif section = "form">
				
			<form action="${url.loginAction}" method="post">
                
                <div>
                    <h2 id="${properties.kcFormHeader!}">${msg("joinCompanyText")}</h2> 
                </div>

                <div>
	
					<div>
                     <label class="${properties.kcInputLabel!}">Full name</label>

						<input type="tel" id="full_name" name="full_name" class="${properties.kcCustomInput!}"
									value="${full_name!}" placeholder="Full name" required aria-invalid="<#if messagesPerField.existsError('full_name')>true</#if>"/>
				
             <#if messagesPerField.existsError('full_name')>
	
								<span id="input-error-full-name" class="${properties.kcInputFieldErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('full_name'))?no_esc}
								</span>
              </#if>

					</div>
	
                    <div>

                     <label class="${properties.kcInputLabel!}">Username</label> 

						<input type="tel" id="username" name="username" class="${properties.kcCustomInput!}"
									value="${username!}" placeholder="Username" required aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"/>
             
				<#if messagesPerField.existsError('username')>
								<span id="input-error-username" class="${properties.kcInputFieldErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('username'))?no_esc}
								</span>
              	</#if>

					</div> 

	<div>
                     <label class="${properties.kcInputLabel!}">Display name</label>

						<input type="tel" id="display_name" name="display_name" class="${properties.kcCustomInput!}"
									value="${display_name!}" placeholder="Display name" required aria-invalid="<#if messagesPerField.existsError('display_name')>true</#if>"/>

             

<#if messagesPerField.existsError('display_name')>
	
								<span id="input-error-display_name" class="${properties.kcInputFieldErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('display_name'))?no_esc}
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

					<div class="form-number-dot-selected"></div>
					<div class="form-number-dot"></div>
					<div class="form-number-dot"></div> 
					
				</div>
				</div>

				
			</form>
    </#if>
</@layout.registrationLayout> 