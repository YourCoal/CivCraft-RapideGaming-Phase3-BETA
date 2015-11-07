package pvptimer;

import java.util.Date;

import com.civcraft.config.CivSettings;
import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.util.CivColor;
import com.civcraft.util.DateUtil;

public class PvPTimer implements Runnable {

	@Override
	public void run() {
		
		for (Resident resident : CivGlobal.getResidents()) {
			if (!resident.isProtected()) {
				continue;
			}
			
			int mins;
			try {
				mins = CivSettings.getInteger(CivSettings.civConfig, "global.pvp_timer");
				if (DateUtil.isAfterMins(new Date(resident.getRegistered()), mins)) {
				//if (DateUtil.isAfterSeconds(new Date(resident.getRegistered()), mins)) {
					resident.setisProtected(false);
					CivMessage.send(resident, CivColor.LightGray+"Your PvP protection has expired.");
				}
			} catch (InvalidConfiguration e) {
				e.printStackTrace();
				return;
			}
		}
	}
}
