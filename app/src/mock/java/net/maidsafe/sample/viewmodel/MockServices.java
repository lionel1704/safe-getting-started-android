package net.maidsafe.sample.viewmodel;

import android.net.Uri;
import android.system.Os;
import android.util.Log;

import net.maidsafe.api.Authenticator;
import net.maidsafe.api.model.AuthIpcRequest;
import net.maidsafe.api.model.IpcRequest;
import net.maidsafe.sample.services.AsyncOperation;
import net.maidsafe.sample.services.Result;
import net.maidsafe.sample.services.SafeApi;

public final class MockServices {

    private static final String LOCATOR = "locator";
    private static final String PASSWORD = "password";
    private static final String INVITE = "invite";
    private static final String ACCOUNT_EXISTS_CODE = "-102";

    private MockServices() {

    }

    public static Uri mockAuthenticate(final String uri) throws Exception {
        Authenticator authenticator = null;
        try {
            authenticator = Authenticator.createAccount(LOCATOR, PASSWORD, INVITE).get();
            Log.d("STAGE:", "Account created");
        } catch (Exception e) {
            if (e.getMessage().contains(ACCOUNT_EXISTS_CODE)) {
                authenticator = Authenticator.login(LOCATOR, PASSWORD).get();
                Log.d("STAGE:", "Logged in to existing account");
            } else {
                Log.e("ERROR:", e.getMessage());
            }
        }

        if (authenticator == null) {
            throw new java.lang.Exception("Not logged in!" + "\nMOCK VAULT PATH: " + Os.getenv("SAFE_MOCK_VAULT_PATH"));
        }
        final String data = uri.replaceAll(".*\\/+", "");
        final IpcRequest request = authenticator.decodeIpcMessage(data).get();
        final AuthIpcRequest authIpcRequest = (AuthIpcRequest) request;
        final String response = authenticator.encodeAuthResponse(authIpcRequest, true).get();
        final String appId = authIpcRequest.getAuthReq().getApp().getId();
        return Uri.parse(appId + "://" + response);
    }

    public static void simulateDisconnect() {
        new AsyncOperation(loading -> {

        }).execute(() -> {
            try {
                final SafeApi api = SafeApi.getInstance(null);
                api.disconnect();
                return new Result();
            } catch (Exception e) {
                return new Result(e);
            }
        }).onResult(result -> {

        }).onException(e -> {
           Log.d("ERROR", e.getMessage());
        });
    }
}
