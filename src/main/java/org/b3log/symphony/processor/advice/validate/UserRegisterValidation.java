/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.symphony.processor.advice.validate;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.b3log.latke.Keys;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.advice.BeforeRequestProcessAdvice;
import org.b3log.latke.servlet.advice.RequestProcessAdviceException;
import org.b3log.latke.util.Strings;
import org.b3log.symphony.processor.CaptchaProcessor;
import org.json.JSONObject;

/**
 * UserRegisterValidation for validate {@link org.b3log.symphony.processor.LoginProcessor} register(Type POST) method.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.6, Nov 15, 2013
 */
@Named
@Singleton
public class UserRegisterValidation extends BeforeRequestProcessAdvice {

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;
    /**
     * Max user name length.
     */
    public static final int MAX_USER_NAME_LENGTH = 20;
    /**
     * Min user name length.
     */
    public static final int MIN_USER_NAME_LENGTH = 1;
    /**
     * Max password length.
     * 
     * <p>MD5 32</p>
     */
    private static final int MAX_PWD_LENGTH = 32;
    /**
     * Min password length.
     */
    private static final int MIN_PWD_LENGTH = 1;
    /**
     * Captcha length.
     */
    private static final int CAPTCHA_LENGTH = 4;

    @Override
    public void doAdvice(final HTTPRequestContext context, final Map<String, Object> args) throws RequestProcessAdviceException {

        final HttpServletRequest request = context.getRequest();

        JSONObject requestJSONObject;
        try {
            final String json = (String) request.getParameterMap().keySet().iterator().next();
            requestJSONObject = new JSONObject(json);
        } catch (final Exception e) {
            throw new RequestProcessAdviceException(new JSONObject().put(Keys.MSG, e.getMessage()));
        }
        final String name = requestJSONObject.optString(User.USER_NAME);
        final String email = requestJSONObject.optString(User.USER_EMAIL);
        final String password = requestJSONObject.optString(User.USER_PASSWORD);
        final String captcha = requestJSONObject.optString(CaptchaProcessor.CAPTCHA);

        checkField(invalidCaptcha(captcha, request), "registerFailLabel", "captchaErrorLabel");
        checkField(invalidUserName(name), "registerFailLabel", "invalidUserNameLabel");
        checkField(!Strings.isEmail(email), "registerFailLabel", "invalidEmailLabel");
        checkField(invalidUserPassword(password), "registerFailLabel", "invalidPasswordLabel");
    }

    /**
     * Checks whether the specified name is invalid.
     * 
     * <p>
     * A valid user name:
     *   <ul>
     *     <li>length [1, 20]</li>
     *     <li>content {a-z, A-Z, 0-9, _}</li>
     *   </ul>
     * </p>
     * 
     * @param name the specified name
     * @return {@code true} if it is invalid, returns {@code false} otherwise
     */
    public static boolean invalidUserName(final String name) {
        final int length = name.length();
        if (length < MIN_USER_NAME_LENGTH || length > MAX_USER_NAME_LENGTH) {
            return true;
        }

        char c;
        for (int i = 0; i < length; i++) {
            c = name.charAt(i);

            if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || '0' <= c && c <= '9' || '_' == c) {
                continue;
            }

            return true;
        }

        return false;
    }

    /**
     * Checks password, length [1, 16].
     * 
     * @param password the specific password
     * @return {@code true} if it is invalid, returns {@code false} otherwise
     */
    public static boolean invalidUserPassword(final String password) {
        return password.length() < MIN_PWD_LENGTH || password.length() > MAX_PWD_LENGTH;
    }

    /**
     * Checks whether the specified captcha is invalid.
     * 
     * @param captcha the specified captcha
     * @param request the specified request
     * @return {@code true} if it is invalid, returns {@code false} otherwise
     */
    public static boolean invalidCaptcha(final String captcha, final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (null == session) {
            return true;
        }

        if (Strings.isEmptyOrNull(captcha) || captcha.length() != CAPTCHA_LENGTH) {
            return true;
        }

        final String storedCaptcha = (String) session.getAttribute(CaptchaProcessor.CAPTCHA);
        if (null == storedCaptcha || !storedCaptcha.equals(captcha)) {
            return true;
        }

        session.removeAttribute(CaptchaProcessor.CAPTCHA);

        return false;
    }

    /**
     * Checks field.
     * 
     * @param invalid the specified invalid flag
     * @param failLabel the specified fail label
     * @param fieldLabel the specified field label
     * @throws RequestProcessAdviceException request process advice exception
     */
    private void checkField(final boolean invalid, final String failLabel, final String fieldLabel)
            throws RequestProcessAdviceException {
        if (invalid) {
            throw new RequestProcessAdviceException(new JSONObject().put(Keys.MSG, langPropsService.get(failLabel)
                                                                                   + " - " + langPropsService.get(fieldLabel)));
        }
    }
}
