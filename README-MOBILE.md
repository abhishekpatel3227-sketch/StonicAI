# Build Stonic AI using only an Android phone

This project includes a GitHub Actions workflow that builds the APK in GitHub's cloud. You do not need Android Studio on your phone.

1. In Chrome, open https://github.com and create/sign in to a free account.
2. Create a **new public repository** named `StonicAI`. Do not add a README or license at repository creation.
3. On the repository page, use **Add file → Upload files**. Upload the *contents* of this `StonicAI` folder (not the outer ZIP). Include the hidden `.github/workflows/android.yml` file and keep its folder path exactly `.github/workflows/android.yml`.
4. Commit the upload to the `main` branch.
5. Open the repository's **Actions** tab. Select **Build Stonic AI APK** and tap **Run workflow** if it has not started automatically.
6. Wait for the run to finish with a green check. Open that run, scroll to **Artifacts**, and download `StonicAI-debug-apk`.
7. Extract the downloaded artifact ZIP. Tap `app-debug.apk` to install. Android may ask you to allow installs from Chrome or Files; only allow it for the app you used to download the APK, then install.

Notes:
- GitHub's free Actions allowance is subject to account limits and public-repository policy.
- This is an original starter app, not the official Stonic product.
- Current chat responses are demo replies, not a connected real AI model. Voice input/output, local memory and browser search handoff are starter features.
- Never publish API keys, passwords, or personal data in a public repository.
