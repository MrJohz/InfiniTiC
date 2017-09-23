package lakmoore.infinitic.client;

import java.io.ByteArrayInputStream;

import lakmoore.infinitic.InfiniTiC;
import lakmoore.infinitic.lib.data.LocalizationJSON;
import lakmoore.infinitic.lib.data.MaterialData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.text.translation.LanguageMap;

public class ResourceManager implements IResourceManagerReloadListener {

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		String data = "";

		for (MaterialData mat : InfiniTiC.MATERIALS) {
			if (mat.json == null) continue;
			for (LocalizationJSON locale : mat.json.localizations) {
				if (locale.locale.toLowerCase()
						.equals(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode().toLowerCase())) {
					data += "material." + mat.json.name.toLowerCase() + ".name=" + locale.solid + "\n";
					data += "fluid.infinitic." + mat.json.name.toLowerCase() + ".name=" + locale.liquid + "\n";

				}
			}
		}

		ByteArrayInputStream inputstream = new ByteArrayInputStream(data.getBytes());
		LanguageMap.inject(inputstream);

	}

}
